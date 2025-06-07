package com.sunnysuperman.mountain.cache.converter;

import java.nio.charset.StandardCharsets;

public class Encoder {

	private Encoder() {
	}

	public static byte[] encode(final String str) {
		return str.getBytes(StandardCharsets.UTF_8);
	}

	public static String encode(final byte[] data) {
		return new String(data, StandardCharsets.UTF_8);
	}

}
