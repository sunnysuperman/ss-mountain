package com.sunnysuperman.mountain.db;

public class DBProperties {
	// 扫描包路径
	private String[] scanPackages;
	// 是否日志
	private boolean log;
	// 是否懒加载
	private boolean lazyInit;

	public String[] getScanPackages() {
		return scanPackages;
	}

	public void setScanPackages(String[] scanPackages) {
		this.scanPackages = scanPackages;
	}

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public boolean isLazyInit() {
		return lazyInit;
	}

	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

}
