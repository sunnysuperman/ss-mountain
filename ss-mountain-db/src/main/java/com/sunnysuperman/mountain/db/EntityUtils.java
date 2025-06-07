package com.sunnysuperman.mountain.db;

public class EntityUtils extends EntityManager {

	private EntityUtils() {
	}

	public static <T> void copyNotUpdatableFields(T src, T dest) {
		EntityManager.copyNotUpdatableFields(src, dest);
	}

}
