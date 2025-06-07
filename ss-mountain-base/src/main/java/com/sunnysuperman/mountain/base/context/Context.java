package com.sunnysuperman.mountain.base.context;

public class Context {
	private String requestIp;
	private Object role;
	private Object user;
	private Object payload;

	public String getRequestIp() {
		return requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	public Object getRole() {
		return role;
	}

	public void setRole(Object role) {
		this.role = role;
	}

	public Object getUser() {
		return user;
	}

	public void setUser(Object user) {
		this.user = user;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

}
