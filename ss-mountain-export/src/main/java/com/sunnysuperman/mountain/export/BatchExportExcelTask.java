package com.sunnysuperman.mountain.export;

import java.util.List;

import com.sunnysuperman.mountain.base.ComponentManager;
import com.sunnysuperman.mountain.lang.loop.LoopExecutor;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;

public abstract class BatchExportExcelTask<T> extends ExportExcelTask<GenericExportJob> {

	private int batchNum;

	protected BatchExportExcelTask(GenericExportJob job, int batchNum) {
		super(job, ComponentManager.get(GenericExportJobRepository.class));
		this.batchNum = batchNum;
	}

	@Override
	protected final void writeData() {
		beforeWrite();

		new MyLoopExecutor().run();
	}

	protected void beforeWrite() {
		// nope
	}

	protected abstract PullPage<T> loadData(PullPageRequest pageRequest);

	protected abstract void writeBatch(List<T> items);

	private class MyLoopExecutor extends LoopExecutor<T> {

		@Override
		protected PullPage<T> loadData(PullPageRequest pageRequest) {
			return BatchExportExcelTask.this.loadData(pageRequest);
		}

		@Override
		protected void executeBatch(List<T> items) {
			BatchExportExcelTask.this.writeBatch(items);
		}

		@Override
		protected int getLimit() {
			return batchNum;
		}

	}

}
