package com.sunnysuperman.mountain.swagger2;

public class SwaggerProperties {
	private boolean enabled;
	private String[] scanPackages;

	public String getPathMatchPattern() {
		return "/swagger-ui/**";
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String[] getScanPackages() {
		return scanPackages;
	}

	public void setScanPackages(String[] scanPackages) {
		this.scanPackages = scanPackages;
	}

}
