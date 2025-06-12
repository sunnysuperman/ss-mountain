package com.sunnysuperman.mountain.excel;

public enum ExcelCellType {
	STRING(1), INT(2), FLOAT(3), DOUBLE(4), LONG(5), DATE(6);

	private byte value;

	private ExcelCellType(int value) {
		this.value = (byte) value;
	}

	public byte value() {
		return value;
	}

	public static ExcelCellType find(byte value) {
		for (ExcelCellType item : ExcelCellType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		return null;
	}
}