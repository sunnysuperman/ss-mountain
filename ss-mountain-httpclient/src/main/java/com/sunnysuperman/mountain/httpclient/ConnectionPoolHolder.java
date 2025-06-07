package com.sunnysuperman.mountain.httpclient;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;

public class ConnectionPoolHolder {
	ConnectionPool pool;

	public ConnectionPoolHolder(int maxIdleConnections, long keepAliveDuration) {
		super();
		this.pool = new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
	}

	public ConnectionPool get() {
		return pool;
	}

}
