package com.sunnysuperman.mountain.lang.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.utils.Base62;

class Base62Test {

	@Test
	void test() {
		codec(1);
		codec(101000);
		codec(9999999999L);
	}

	private void codec(long base10) {
		String base62 = Base62.fromBase10(base10);
		System.out.println(base62);
		long anotherBase10 = Base62.toBase10(base62);
		assertEquals(base10, anotherBase10);
	}

}
