package com.sunnysuperman.mountain.excel.reader;

public class ExcelReaderOptions {
	private boolean streaming = true; // 是否用流形式
	private int streamingRowCacheSize = 100; // 流式读取条数
	private Handler handler; // 数据处理器
	private ExcelColumn[] columns; // 列
	private boolean columnsInOrder; // 列顺序是否需要保持一致
	private boolean firstRowNumAsOne; // 首行行号是否以1开始
	private boolean copy; // 读取的同时拷贝到另一个表格里
	private boolean copyRowCache = true; // 拷贝行缓存
	private int copyRowCacheSize = 0; // 拷贝行缓存条数：数据批处理条数+N条空数据(防止空数据过多，导致拷贝再改行数据时空指针)

	public boolean isStreaming() {
		return streaming;
	}

	public ExcelReaderOptions setStreaming(boolean streaming) {
		this.streaming = streaming;
		return this;
	}

	public int getStreamingRowCacheSize() {
		return streamingRowCacheSize;
	}

	public ExcelReaderOptions setStreamingRowCacheSize(int streamingRowCacheSize) {
		this.streamingRowCacheSize = streamingRowCacheSize;
		return this;
	}

	public Handler getHandler() {
		return handler;
	}

	public ExcelReaderOptions setHandler(Handler handler) {
		this.handler = handler;
		if (handler instanceof BatchHandler && copy && copyRowCache && copyRowCacheSize <= 0) {
			this.copyRowCacheSize = Math.round((((BatchHandler<?>) handler).getBatchNum()) * 1.2f);
		}
		return this;
	}

	public ExcelColumn[] getColumns() {
		return columns;
	}

	public ExcelReaderOptions setColumns(ExcelColumn[] columns) {
		this.columns = columns;
		return this;
	}

	public boolean isColumnsInOrder() {
		return columnsInOrder;
	}

	public ExcelReaderOptions setColumnsInOrder(boolean columnsInOrder) {
		this.columnsInOrder = columnsInOrder;
		return this;
	}

	public boolean isFirstRowNumAsOne() {
		return firstRowNumAsOne;
	}

	public ExcelReaderOptions setFirstRowNumAsOne(boolean firstRowNumAsOne) {
		this.firstRowNumAsOne = firstRowNumAsOne;
		return this;
	}

	public boolean isCopy() {
		return copy;
	}

	public ExcelReaderOptions setCopy(boolean copy) {
		this.copy = copy;
		return this;
	}

	public boolean isCopyRowCache() {
		return copyRowCache;
	}

	public ExcelReaderOptions setCopyRowCache(boolean copyRowCache) {
		this.copyRowCache = copyRowCache;
		return this;
	}

	public int getCopyRowCacheSize() {
		return copyRowCacheSize;
	}

	public ExcelReaderOptions setCopyRowCacheSize(int copyRowCacheSize) {
		this.copyRowCacheSize = copyRowCacheSize;
		this.copy = true;
		return this;
	}
}
