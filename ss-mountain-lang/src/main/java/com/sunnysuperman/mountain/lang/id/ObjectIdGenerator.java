package com.sunnysuperman.mountain.lang.id;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectIdGenerator {
	private final AtomicInteger counter = new AtomicInteger(new SecureRandom().nextInt());

	ObjectIdGenerator() {
	}

	public String generate() {
		return new ObjectId(counter).toHexString();
	}

}