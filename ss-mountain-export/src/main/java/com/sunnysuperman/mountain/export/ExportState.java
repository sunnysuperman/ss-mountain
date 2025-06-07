package com.sunnysuperman.mountain.export;

import com.sunnysuperman.mountain.lang.enums.CodeAwareEnum;

public enum ExportState implements CodeAwareEnum {

	INIT(0),

	ING(1),

	SUCCESS(2),

	ERROR(4);

	private byte code;

	private ExportState(int code) {
		this.code = (byte) code;
	}

	@Override
	public byte code() {
		return code;
	}
}