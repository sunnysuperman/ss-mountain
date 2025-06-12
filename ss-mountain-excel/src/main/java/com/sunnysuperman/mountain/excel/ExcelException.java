package com.sunnysuperman.mountain.excel;

public class ExcelException extends Exception {
	private static final long serialVersionUID = -1L;

	public static final int ERROR_NOT_AN_EXCEL_FILE = 1;
	public static final int ERROR_DATA_IS_EMPTY = 2;
	public static final int ERROR_COULD_NOT_FIND_COLUMN = 3;
	public static final int ERROR_UNKNOWN_CELL_TYPE = 4;
	public static final int ERROR_COLUMN_NOT_MATCH = 5;
	public static final int ERROR_CELL_TYPE_NOT_MATCH = 6;

	private final transient int errorCode;
	private final transient Object[] errorParams;

	public ExcelException(int errorCode) {
		super();
		this.errorCode = errorCode;
		this.errorParams = null;
	}

	public ExcelException(int errorCode, Object... errorParams) {
		super();
		this.errorCode = errorCode;
		this.errorParams = errorParams;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public Object[] getErrorParams() {
		return errorParams;
	}

}