package com.sunnysuperman.mountain.base;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Valid
@Component
@ConfigurationProperties("ss-mountain.base")
public class BaseProperties {
	// 是否本地开发模式(框架会根据本地模式做一些特殊处理，如启动校验、本地消息）
	private boolean local;
	// 是否严格模式
	private boolean strictMode = true;
	// 扫描包
	@NotEmpty
	private String[] scanPackages;

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public boolean isStrictMode() {
		return strictMode;
	}

	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
	}

	public String[] getScanPackages() {
		return scanPackages;
	}

	public void setScanPackages(String[] scanPackages) {
		this.scanPackages = scanPackages;
	}

}
