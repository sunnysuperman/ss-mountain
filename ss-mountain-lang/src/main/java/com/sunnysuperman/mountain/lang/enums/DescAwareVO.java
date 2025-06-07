package com.sunnysuperman.mountain.lang.enums;

public class DescAwareVO {
	private byte code;
	private String desc;

	public DescAwareVO() {
	}

	public DescAwareVO(byte code, String desc) {
		super();
		this.code = code;
		this.desc = desc;
	}

	public byte getCode() {
		return code;
	}

	public void setCode(byte code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}