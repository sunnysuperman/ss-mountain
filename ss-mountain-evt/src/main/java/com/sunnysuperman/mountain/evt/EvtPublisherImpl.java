package com.sunnysuperman.mountain.evt;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.exception.Exceptions;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.mq.MQProduceOptions;
import com.sunnysuperman.mountain.mq.MQProducer;
import com.sunnysuperman.mountain.mq.MQProducerManager;
import com.sunnysuperman.mountain.task.TaskExecutor;
import com.sunnysuperman.mountain.task.TaskHelper;
import com.sunnysuperman.mountain.transaction.TransactionAwareWork;
import com.sunnysuperman.mountain.transaction.TransactionHelper;

public class EvtPublisherImpl implements EvtPublisher {
	private static final Logger LOG = LoggerFactory.getLogger(EvtPublisherImpl.class);

	private EvtProperties evtProperties;
	private EvtRepositoryManager evtRepositoryManager;
	private EvtConsumer evtConsumer;
	private TaskHelper taskHelper;
	private TransactionHelper transactionHelper;
	private MQProducerManager mqProducerManager;

	/** 任务执行器 **/
	private TaskExecutor taskExecutor;

	public EvtPublisherImpl(EvtProperties evtProperties, EvtRepositoryManager evtRepositoryManager,
			EvtConsumer evtConsumer, TaskHelper taskHelper, TransactionHelper transactionHelper,
			MQProducerManager mqProducerManager) {
		super();
		this.evtProperties = evtProperties;
		this.evtRepositoryManager = evtRepositoryManager;
		this.evtConsumer = evtConsumer;
		this.taskHelper = taskHelper;
		this.transactionHelper = transactionHelper;
		this.mqProducerManager = mqProducerManager;
		if (!evtProperties.isMqDisabled() && mqProducerManager == null) {
			throw Exceptions.wrapRuntimeException("Require mqProducerManager if ev.mqDisabled = false ");
		}
	}

	@PostConstruct
	public void init() {
		taskExecutor = TaskExecutor.of(taskHelper, evtProperties.getTaskExecutor());
	}

	/**
	 * 保存并发布事件
	 * 
	 * @param event 事件
	 */
	@Override
	public <T extends Evt> void publish(T event) {
		transactionHelper.doWithoutTransaction(new EvtTransactionWork<>(event, true));
	}

	/**
	 * 保存并发布事件
	 * 
	 * @param events 事件列表
	 */
	@Override
	public <T extends Evt> void publishBatch(List<T> events) {
		if (events.isEmpty()) {
			return;
		}
		if (events.size() == 1) {
			// 只有1个事件，就不使用事务
			publish(events.get(0));
		} else {
			// 多个事件，使用事务保存
			transactionHelper.doTransaction(new EvtTransactionWork<>(events, true));
		}
	}

	@Override
	public <T extends Evt> TransactionAwareWork publishInTransaction(Supplier<T> eventMaker) {
		return new EvtTransactionWork<T>(true).setEventMaker(eventMaker);
	}

	@Override
	public <T extends Evt> TransactionAwareWork publishBatchInTransaction(Supplier<List<T>> eventsMaker) {
		return new EvtTransactionWork<T>(true).setEventsMaker(eventsMaker);
	}

	private class EvtTransactionWork<T extends Evt> implements TransactionAwareWork {
		List<T> events;
		Supplier<T> eventMaker;
		Supplier<List<T>> eventsMaker;
		boolean shouldPublish;

		private EvtTransactionWork(boolean shouldPublish) {
			this.shouldPublish = shouldPublish;
		}

		public EvtTransactionWork(T event, boolean shouldPublish) {
			super();
			this.events = Collections.singletonList(event);
			this.shouldPublish = shouldPublish;
		}

		public EvtTransactionWork(List<T> events, boolean shouldPublish) {
			super();
			this.events = events;
			this.shouldPublish = shouldPublish;
		}

		public EvtTransactionWork<T> setEventMaker(Supplier<T> eventMaker) {
			this.eventMaker = eventMaker;
			return this;
		}

		public EvtTransactionWork<T> setEventsMaker(Supplier<List<T>> eventsMaker) {
			this.eventsMaker = eventsMaker;
			return this;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void doTransaction() {
			if (events == null || events.isEmpty()) {
				if (eventMaker != null) {
					T e = eventMaker.get();
					if (e == null) {
						return;
					}
					events = Collections.singletonList(e);
				} else if (eventsMaker != null) {
					events = eventsMaker.get();
				}
				if (events == null || events.isEmpty()) {
					return;
				}
			}
			EvtRepository repo = evtRepositoryManager.getRepository(events.get(0));
			if (events.size() > 1) {
				repo.insertBatch(events);
			} else {
				repo.insert(events.get(0));
			}
		}

		@Override
		public void afterTransaction() {
			if (!shouldPublish) {
				return;
			}
			if (events == null || events.isEmpty()) {
				return;
			}
			long now = System.currentTimeMillis();
			for (T event : events) {
				try {
					doPublish(event, now);
				} catch (Exception ex) {
					LOG.error(null, ex);
				}
			}
		}

		private void doPublish(T event, long now) {
			EvtConf conf = event.getClass().getAnnotation(EvtConf.class);
			if (conf == null) {
				throw new EvtException("No '@EvtConf' defined of event: " + event.getClass());
			}
			EvtPriority priority = conf.priority();
			long delay = event.getScheduledTime().getTime() - now;
			// MQ投递事件
			boolean published = doPublishByMQ(event, conf, delay);
			if (published) {
				return;
			}
			// 对于即时事件，如果未配置MQ或MQ投递事件失败，本机处理事件
			if (delay <= 0) {
				if (priority.higherOrEquals(EvtPriority.HIGHEST)) {
					// 对于最高优先级事件，立即执行
					evtConsumer.consume(event);
				} else {
					// 对于非最高优先级事件，异步线程执行
					EvtConsumeTask task = new EvtConsumeTask(event);
					taskExecutor.execute(task);
				}
			}
		}

		private boolean doPublishByMQ(Evt event, EvtConf conf, long delay) {
			if (evtProperties.isMqDisabled()) {
				return false;
			}
			if (conf.mqDisabled()) {
				return false;
			}
			String queue = conf.mqName();
			MQProducer producer = mqProducerManager.getProducer(queue);
			if (producer == null) {
				throw new EvtException("No MQProducer for queue: " + queue);
			}
			try {
				MQProduceOptions options = new MQProduceOptions();
				options.setTag(Str.or(conf.mqTag(), conf.name()));
				if (delay >= 500) {
					options.setDeliveryTime(event.getScheduledTime());
				}
				// 高优先级事件MQ发送改成同步模式，确保发送成功
				if (conf.priority().higherOrEquals(EvtPriority.HIGH)) {
					options.setSync(Boolean.TRUE);
				}
				producer.produce(queue, event, options);
				return true;
			} catch (Exception ex) {
				LOG.error("Failed to publish event", ex);
				return false;
			}
		}

	}

	private class EvtConsumeTask implements Runnable {
		Evt evt;

		public EvtConsumeTask(Evt evt) {
			super();
			this.evt = evt;
		}

		@Override
		public void run() {
			try {
				evtConsumer.consume(evt);
			} catch (Exception ex) {
				LOG.error(null, ex);
			}
		}

	}

}
