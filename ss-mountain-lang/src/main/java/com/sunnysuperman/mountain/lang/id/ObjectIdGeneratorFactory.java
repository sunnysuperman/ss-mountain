package com.sunnysuperman.mountain.lang.id;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

public final class ObjectIdGeneratorFactory {

	private ObjectIdGeneratorFactory() {
	}

	public static class ObjectIdGenerator {
		private final AtomicInteger counter = new AtomicInteger(new SecureRandom().nextInt());

		public String generate() {
			return new ObjectId(counter).toHexString();
		}
	}

	public static ObjectIdGenerator create() {
		return new ObjectIdGenerator();
	}

}
