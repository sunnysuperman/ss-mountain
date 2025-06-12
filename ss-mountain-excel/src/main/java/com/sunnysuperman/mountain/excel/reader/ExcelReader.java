package com.sunnysuperman.mountain.excel.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sunnysuperman.mountain.excel.ExcelException;
import com.sunnysuperman.mountain.excel.ExcelUtils;
import com.sunnysuperman.mountain.lang.utils.IOUtil;
import com.sunnysuperman.mountain.lang.utils.Str;

@SuppressWarnings("squid:S3776")
public class ExcelReader {
	private File file; // 读取文件
	private Sheet sheet; // 读取表格
	private Sheet copySheet; // 读取表格
	private ExcelReaderOptions options;

	private int[] columnIndexes;
	private Workbook workbook; // 原始工作簿
	private Workbook copyWorkbook; // 拷贝工作簿

	public ExcelReader(File file, ExcelReaderOptions options) {
		super();
		this.file = file;
		this.options = options;
	}

	public ExcelReader(Sheet sheet, ExcelReaderOptions options) {
		super();
		this.sheet = sheet;
		this.options = options;
	}

	private void loadSheet() throws ExcelException {
		if (sheet != null) {
			return;
		}
		if (options.isStreaming() && options.getStreamingRowCacheSize() <= 0) {
			throw new IllegalArgumentException("streamingRowCacheSize");
		}
		workbook = ExcelUtils.loadWorkbook(file, options.isStreaming() ? options.getStreamingRowCacheSize() : 0);
		sheet = ExcelUtils.ensureSheet(workbook, 0);

		if (options.isCopy() && copySheet == null) {
			copyWorkbook = options.getCopyRowCacheSize() == 0 ? ExcelUtils.newWorkbook()
					: ExcelUtils.newWorkbook(options.getCopyRowCacheSize());
			copySheet = ExcelUtils.ensureSheet(copyWorkbook, 0);
		}
	}

	public void read() throws ExcelException, HandlerException {
		boolean ok = false;
		try {
			// 准备表格
			loadSheet();
			// 读取
			doRead();
			ok = true;
		} finally {
			if (!ok && copyWorkbook != null) {
				// 如果出错了，关闭自动生成的拷贝工作簿，否则不关闭（另有用途，如写入到文件等）
				IOUtil.close(copyWorkbook);
			}
			// 原始工作簿，需要关闭（如果是传入sheet的不关闭，交由调用方自己关闭）
			if (workbook != null && file != null) {
				IOUtil.close(workbook);
			}
		}
	}

	private void doRead() throws ExcelException, HandlerException {
		// 开始
		boolean streaming = options.isStreaming();
		Handler handler = options.getHandler();
		int firstRow = streaming ? 0 : sheet.getFirstRowNum();
		int lastRow = streaming ? -1 : sheet.getLastRowNum();
		boolean ok = handler.onStart(this, lastRow - firstRow);
		if (!ok) {
			return;
		}
		// 逐行读取数据
		if (streaming) {
			for (Row row : sheet) {
				if (row != null) {
					if (columnIndexes == null) {
						columnIndexes = readHeader(row);
					} else {
						ok = readRow(row);
					}
					if (!ok) {
						break;
					}
				}
			}
		} else {
			columnIndexes = readHeader(sheet.getRow(firstRow));
			for (int i = firstRow + 1; i <= lastRow; i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					ok = readRow(row);
					if (!ok) {
						break;
					}
				}
			}
		}
		// 结束
		handler.onEnd(this);
	}

	private int[] readHeader(Row row) throws ExcelException, HandlerException {
		Handler handler = options.getHandler();
		ExcelColumn[] columns = options.getColumns();
		int[] indexes = new int[columns.length];
		if (options.isColumnsInOrder()) {
			for (int i = 0; i < columns.length; i++) {
				Cell cell = row.getCell(i);
				String cellValue = null;
				if (cell != null) {
					try {
						cellValue = cell.getStringCellValue();
					} catch (Exception ex) {
						// ignore
					}
				}
				if (cellValue == null || !cellValue.equals(columns[i].getTitle())) {
					throw new ExcelException(ExcelException.ERROR_COLUMN_NOT_MATCH, i);
				}
				indexes[i] = i;
			}
		} else {
			for (int i = 0; i < columns.length; i++) {
				int index = findCellIndex(row, columns[i]);
				if (index < 0) {
					throw new ExcelException(ExcelException.ERROR_COULD_NOT_FIND_COLUMN, i);
				}
				indexes[i] = index;
			}
		}
		if (copySheet != null) {
			copyRow(row, true);
		}
		handler.onHeaderRead(this, row);
		return indexes;
	}

	private boolean readRow(Row row) throws HandlerException {
		if (row == null) {
			return true;
		}
		if (copySheet != null) {
			copyRow(row, false);
		}
		Map<String, Object> data = new HashMap<>();
		List<ExcelColumn> errorColumns = readRow(data, row);
		return options.getHandler().onData(this, data,
				options.isFirstRowNumAsOne() ? row.getRowNum() + 1 : row.getRowNum(), errorColumns);
	}

	private void copyRow(Row row, boolean isHeader) throws HandlerException {
		Row copyRow = copySheet.createRow(row.getRowNum());
		ExcelUtils.copyRow(row, copyRow);
		options.getHandler().onRowCopied(this, row, copyRow, isHeader);
	}

	private List<ExcelColumn> readRow(Map<String, Object> data, Row row) {
		ExcelColumn[] columns = options.getColumns();
		List<ExcelColumn> errorColumns = null;
		for (int k = 0; k < columnIndexes.length; k++) {
			int index = columnIndexes[k];
			Cell cell = index < 0 ? null : row.getCell(index);
			if (cell == null) {
				continue;
			}
			ExcelColumn column = columns[k];
			Object value;
			try {
				value = ExcelUtils.getCellValue(cell, column.getType());
			} catch (Exception ex) {
				try {
					value = ExcelUtils.getCellValue(cell);
				} catch (Exception ex2) {
					value = null;
				}
				if (errorColumns == null) {
					errorColumns = new ArrayList<>(columns.length);
				}
				errorColumns.add(column);
			}
			data.put(column.getKey(), value);
		}
		return errorColumns;
	}

	private int findCellIndex(Row row, ExcelColumn column) throws ExcelException {
		int firstCell = row.getFirstCellNum();
		int lastCell = row.getLastCellNum();
		for (int i = firstCell; i <= lastCell; i++) {
			Cell cell = row.getCell(i);
			if (cell == null) {
				continue;
			}
			String title = Str.trimToEmpty(ExcelUtils.getStringCellValue(cell));
			switch (column.getMatchMode()) {
			case EXACTLY:
				if (column.getTitle().equals(title)) {
					return i;
				}
				break;
			case STARTS_WITH:
				/* XX(*) startsWith XX */
				if (column.getTitle().startsWith(title)) {
					return i;
				}
				break;
			case FUZZY:
				/* XY indexOf XXY */
				if (column.getTitle().indexOf(title) >= 0) {
					return i;
				}
				break;
			default:
				break;
			}
		}
		return -1;
	}

	public File getFile() {
		return file;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public Sheet getCopySheet() {
		return copySheet;
	}

	public ExcelReader setCopySheet(Sheet copySheet) {
		this.copySheet = copySheet;
		return this;
	}

}