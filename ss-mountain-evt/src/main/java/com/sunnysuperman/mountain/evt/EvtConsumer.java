package com.sunnysuperman.mountain.evt;

import java.util.List;

import com.sunnysuperman.mountain.evt.EvtListenerManager.Listener;
import com.sunnysuperman.mountain.lock.LockHelper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EvtConsumer {
	private EvtListenerManager evtListenerManager;
	private EvtRepositoryManager evtRepositoryManager;
	private LockHelper lockHelper;

	public EvtConsumer(EvtListenerManager evtListenerManager, EvtRepositoryManager evtRepositoryManager,
			LockHelper lockHelper) {
		super();
		this.evtListenerManager = evtListenerManager;
		this.evtRepositoryManager = evtRepositoryManager;
		this.lockHelper = lockHelper;
	}

	public boolean consume(Evt evt) throws EvtException {
		Class<? extends Evt> evtClass = evt.getClass();
		List<Listener> listeners = evtListenerManager.getListeners(evtClass);
		try {
			if (evt.getId() == null) {
				return doConsume(evt, listeners);
			} else {
				EvtConf conf = evt.getClass().getAnnotation(EvtConf.class);
				String lockKey = "@evt:" + conf.name() + ":" + evt.getId().toString();
				int timeout = conf.consumeTimeoutInSeconds();
				EvtHandler handler = new EvtHandler(evt, listeners);
				boolean executed = lockHelper.tryLock(lockKey, handler, 0, timeout, false);
				return executed && handler.isDone();
			}
		} catch (EvtException e) {
			throw e;
		} catch (Exception ex) {
			throw new EvtException(evt, ex);
		}
	}

	private class EvtHandler implements Runnable {
		Evt rawEvt;
		List<Listener> listeners;
		boolean done = false;

		public EvtHandler(Evt rawEvt, List<Listener> listeners) {
			super();
			this.rawEvt = rawEvt;
			this.listeners = listeners;
		}

		@Override
		public void run() {
			EvtRepository evtRepository = evtRepositoryManager.getRepository(rawEvt);
			Evt evt = (Evt) evtRepository.findById(rawEvt.getId());
			if (evt == null) {
				// 任务不存在，有可能被另一个线程处理了
				done = true;
				return;
			}
			try {
				done = doConsume(evt, listeners);
			} finally {
				if (done) {
					// 事件处理完成，删除
					evtRepository.deleteById(evt.getId());
				} else {
					// 事件未处理完成，预约下次执行，如果不再预约(如调用方不想无限执行)就删除
					if (evt.scheduleNext()) {
						evtRepository.update(evt);
					} else {
						evtRepository.deleteById(evt.getId());
					}
				}
			}
		}

		public boolean isDone() {
			return done;
		}
	}

	private boolean doConsume(Evt evt, List<Listener> listeners) {
		for (Listener listener : listeners) {
			if (!listener.consume(evt)) {
				return false;
			}
		}
		return true;
	}
}
