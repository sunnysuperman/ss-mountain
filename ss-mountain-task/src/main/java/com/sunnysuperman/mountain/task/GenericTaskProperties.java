package com.sunnysuperman.mountain.task;

import com.sunnysuperman.mountain.lang.threadpool.ThreadPoolExecutorProperties;

public class GenericTaskProperties extends ThreadPoolExecutorProperties {

	/** 是否开启 **/
	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
