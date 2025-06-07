package com.sunnysuperman.mountain.mq.rocketmq4;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.Obj;
import com.sunnysuperman.mountain.lang.utils.Str;

public class RocketMQ4Properties {
	private String accessKey;
	private String accessSecret;
	private String endpoints;
	private List<RocketMQ4QueueProperties> queues;

	public void validate() {
		if (Str.isEmpty(accessKey)) {
			throw new IllegalArgumentException("accessKey is empty");
		}
		if (Str.isEmpty(accessSecret)) {
			throw new IllegalArgumentException("accessSecret is empty");
		}
		if (Str.isEmpty(endpoints)) {
			throw new IllegalArgumentException("endpoints is empty");
		}
		if (queues == null || queues.isEmpty()) {
			throw new IllegalArgumentException("groups is empty");
		}
		queues.forEach(RocketMQ4QueueProperties::validate);
	}

	@Override
	public String toString() {
		RocketMQ4Properties p;
		try {
			p = Obj.copyProperties(this, getClass().getDeclaredConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new UnexpectedException(e);
		}
		p.setAccessSecret(null);
		return Jsons.write(p);
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public String getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(String endpoints) {
		this.endpoints = endpoints;
	}

	public List<RocketMQ4QueueProperties> getQueues() {
		return queues;
	}

	public void setQueues(List<RocketMQ4QueueProperties> queues) {
		this.queues = queues;
	}

}
