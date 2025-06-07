package com.sunnysuperman.mountain.job.executor;

import java.util.List;

import com.sunnysuperman.mountain.job.JobLogger;
import com.sunnysuperman.mountain.lang.loop.LoopExecutor;
import com.sunnysuperman.mountain.lang.model.DataHolder;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;

public abstract class LoopJobExecutor<T> implements JobExecutor {

	@Override
	public final boolean execute() {
		DataHolder<Boolean> errorHolder = new DataHolder<>(Boolean.FALSE);
		new MyLoopExecutor(errorHolder).run();
		return !errorHolder.get();
	}

	private class MyLoopExecutor extends LoopExecutor<T> {
		DataHolder<Boolean> errorHolder;

		public MyLoopExecutor(DataHolder<Boolean> errorHolder) {
			super();
			this.errorHolder = errorHolder;
		}

		@Override
		protected PullPage<T> loadData(PullPageRequest pageRequest) {
			return findForPullPage(pageRequest);
		}

		@Override
		protected void executeBatch(List<T> items) {
			for (T item : items) {
				try {
					doExecute(item);
				} catch (Exception ex) {
					JobLogger.error(ex);
					errorHolder.set(Boolean.TRUE);
				}
			}
		}

	}

	protected abstract PullPage<T> findForPullPage(PullPageRequest pageRequest);

	protected abstract void doExecute(T job);

}
