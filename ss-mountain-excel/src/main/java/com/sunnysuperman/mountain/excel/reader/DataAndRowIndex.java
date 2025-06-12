package com.sunnysuperman.mountain.excel.reader;

public class DataAndRowIndex<T> {
	private T data;
	private int rowIndex;

	public DataAndRowIndex() {
	}

	public DataAndRowIndex(T data, int rowIndex) {
		this.data = data;
		this.rowIndex = rowIndex;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
}