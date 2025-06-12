package com.sunnysuperman.mountain.excel.reader;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

public interface Handler {

	/** 开始时回调(数据行数，注意流式读取，没有行号) **/
	default boolean onStart(ExcelReader reader, int dataRowsNum) throws HandlerException {
		return true;
	}

	/** 表头读完之后回调 **/
	default void onHeaderRead(ExcelReader reader, Row row) throws HandlerException {
		// nope
	}

	/** 表格行拷贝回调 **/
	default void onRowCopied(ExcelReader reader, Row row, Row copyRow, boolean isHeader) throws HandlerException {
		// nope
	}

	/** 数据处理 **/
	boolean onData(ExcelReader reader, Map<String, Object> data, int rowIndex, List<ExcelColumn> errorColumns)
			throws HandlerException;

	/** 结束回调 **/
	default void onEnd(ExcelReader reader) throws HandlerException {
		// nope
	}

}