package com.sunnysuperman.mountain.excel.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BatchHandler<T> implements Handler {
	int batchNum;
	List<T> list;

	protected BatchHandler(int batchNum) {
		if (batchNum <= 0) {
			throw new IllegalArgumentException("batchNum");
		}
		this.batchNum = batchNum;
		list = new ArrayList<>(batchNum);
	}

	protected BatchHandler() {
		this(500);
	}

	public int getBatchNum() {
		return batchNum;
	}

	@Override
	public final boolean onData(ExcelReader reader, Map<String, Object> data, int rowIndex,
			List<ExcelColumn> errorColumns) throws HandlerException {
		if (errorColumns != null) {
			data = null;
		}
		T item = parseData(reader, data, rowIndex);
		if (item == null) {
			return true;
		}
		list.add(item);
		if (list.size() >= batchNum) {
			handleBatch(reader, list);
			list.clear();
		}
		return true;
	}

	@Override
	public final void onEnd(ExcelReader reader) throws HandlerException {
		if (!list.isEmpty()) {
			handleBatch(reader, list);
			list.clear();
		}
		end(reader);
	}

	protected abstract T parseData(ExcelReader reader, Map<String, Object> data, int rowIndex) throws HandlerException;

	protected abstract void handleBatch(ExcelReader reader, List<T> dataList) throws HandlerException;

	protected void end(ExcelReader reader) throws HandlerException {
		// nope
	}

}