package com.sunnysuperman.mountain.randomid.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.randomid.RandomIdUtils;

class RandomIdUtilsTest {

	@Test
	void testConcat() {
		assertEquals("516273849", RandomIdUtils.concat("56789", "1234"));
		assertEquals("516273849", RandomIdUtils.concat("1234", "56789"));

		assertEquals("51627389", RandomIdUtils.concat("56789", "123"));
		assertEquals("51627389", RandomIdUtils.concat("123", "56789"));

		assertEquals("5162789", RandomIdUtils.concat("56789", "12"));
		assertEquals("5162789", RandomIdUtils.concat("12", "56789"));

		assertEquals("516789", RandomIdUtils.concat("56789", "1"));
		assertEquals("516789", RandomIdUtils.concat("1", "56789"));
	}

	@Test
	void testRandomArrayOfMax() {
		for (int i = 0; i < 20; i++) {
			int[] array = RandomIdUtils.randomArrayOfMax(10);
			System.out.println(Str.join(array));
			List<Integer> list = new ArrayList<>();
			for (int val : array) {
				list.add(val);
			}
			assertEquals(10, list.size());
			assertTrue(list.contains(0));
			assertTrue(list.contains(1));
			assertTrue(list.contains(2));
			assertTrue(list.contains(3));
			assertTrue(list.contains(4));
			assertTrue(list.contains(5));
			assertTrue(list.contains(6));
			assertTrue(list.contains(7));
			assertTrue(list.contains(8));
			assertTrue(list.contains(9));
			assertFalse(list.contains(10));
		}
	}

}
