package com.sunnysuperman.mountain.cache;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public class CachePolicy {
	protected String prefix;
	protected int expireIn;

	public void validate() {
		if (prefix == null) {
			throw new UnexpectedException("Bad prefix");
		}
		if (expireIn <= 0) {
			throw new UnexpectedException("Bad expireIn");
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public CachePolicy setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public int getExpireIn() {
		return expireIn;
	}

	public CachePolicy setExpireIn(int expireIn) {
		this.expireIn = expireIn;
		return this;
	}

}
