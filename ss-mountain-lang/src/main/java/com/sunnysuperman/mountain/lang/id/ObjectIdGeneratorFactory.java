package com.sunnysuperman.mountain.lang.id;

public final class ObjectIdGeneratorFactory {

	private ObjectIdGeneratorFactory() {
	}

	public static ObjectIdGenerator create() {
		return new ObjectIdGenerator();
	}

}
