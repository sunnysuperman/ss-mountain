package com.sunnysuperman.mountain.excel.reader;

import com.sunnysuperman.mountain.excel.ExcelCellType;

public class ExcelColumn {
	private String key;
	private String title;
	private ExcelCellType type;
	private ExcelColumnMatchMode matchMode;

	public ExcelColumn(String key, String title, ExcelCellType type, ExcelColumnMatchMode matchMode) {
		super();
		this.key = key;
		this.title = title == null ? key : title;
		this.type = type;
		this.matchMode = matchMode == null ? ExcelColumnMatchMode.EXACTLY : matchMode;
	}

	public ExcelColumn(String key, String title, ExcelCellType type) {
		this(key, title, type, null);
	}

	public ExcelColumn(String key, String title) {
		this(key, title, ExcelCellType.STRING, null);
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public ExcelCellType getType() {
		return type;
	}

	public ExcelColumnMatchMode getMatchMode() {
		return matchMode;
	}

}