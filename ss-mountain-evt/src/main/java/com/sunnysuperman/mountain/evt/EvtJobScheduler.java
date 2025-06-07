package com.sunnysuperman.mountain.evt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import com.sunnysuperman.mountain.job.JobExecutorManager;
import com.sunnysuperman.mountain.job.executor.LoopJobExecutor;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;
import com.sunnysuperman.mountain.lang.utils.Str;

/** 事件定时任务调度 **/
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EvtJobScheduler {
	private static final Logger LOG = LoggerFactory.getLogger(EvtJobScheduler.class);

	private EvtClassManager evtClassMananger;
	private EvtRepositoryManager evtRepositoryManager;
	private EvtConsumer evtConsumer;

	public EvtJobScheduler(EvtClassManager evtClassMananger, EvtRepositoryManager evtRepositoryManager,
			EvtConsumer evtConsumer) {
		super();
		this.evtClassMananger = evtClassMananger;
		this.evtRepositoryManager = evtRepositoryManager;
		this.evtConsumer = evtConsumer;
	}

	@EventListener(ApplicationReadyEvent.class)
	@Order(100)
	public void onReady(ApplicationReadyEvent event) {
		for (Class<?> clazz : evtClassMananger.getAll()) {
			EvtConf conf = clazz.getAnnotation(EvtConf.class);
			if (conf.jobDisabled()) {
				continue;
			}
			Class<? extends Evt> evtClass = (Class<? extends Evt>) clazz;
			String jobName = Str.isNotEmpty(conf.jobName()) ? conf.jobName() : conf.name() + "-event";
			EvtRepository repository = evtRepositoryManager.getRepository(evtClass);
			LOG.info(">>>>>>[evt] event job registered: {}", jobName);
			JobExecutorManager.add(new EvtJobExecutor(jobName, repository));
		}
	}

	private class EvtJobExecutor extends LoopJobExecutor {
		String jobName;
		EvtRepository repository;

		public EvtJobExecutor(String jobName, EvtRepository repository) {
			super();
			this.jobName = jobName;
			this.repository = repository;
		}

		@Override
		public String name() {
			return jobName;
		}

		@Override
		protected PullPage findForPullPage(PullPageRequest pageRequest) {
			return repository.findForPullPage(pageRequest);
		}

		@Override
		protected void doExecute(Object job) {
			evtConsumer.consume((Evt) job);
		}

	}

}
