package com.sunnysuperman.mountain.cache;

public class CacheOptions {
	private String key;
	private int version;
	private int expireIn;

	public CacheOptions(String key, int version, int expireIn) {
		super();
		this.key = key;
		this.version = version;
		this.expireIn = expireIn;
	}

	public String getKey() {
		return key;
	}

	public int getVersion() {
		return version;
	}

	public int getExpireIn() {
		return expireIn;
	}
}
