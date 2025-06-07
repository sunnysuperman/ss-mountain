package com.sunnysuperman.mountain.lang.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.utils.Colls;

class CollsTest {

	@Test
	void testConcatSet() {
		{
			Set<String> set1 = null;
			Set<String> set2 = null;
			assertTrue(Colls.concat(set1, set2).isEmpty());
		}
		{
			Set<String> set1 = null;
			Set<String> set2 = Collections.emptySet();
			assertTrue(Colls.concat(set1, set2).isEmpty());
		}
		{
			Set<String> set1 = Collections.emptySet();
			Set<String> set2 = null;
			assertTrue(Colls.concat(set1, set2).isEmpty());
		}
		{
			Set<String> set1 = Collections.singleton("abc");
			Set<String> set2 = Collections.singleton("def");
			assertEquals(2, Colls.concat(set1, set2).size());
		}
		{
			Set<String> set1 = Collections.singleton("abc");
			Set<String> set2 = Set.of("def", "g");
			assertEquals(3, Colls.concat(set1, set2).size());
		}
	}

	@Test
	void testIndexOf() {
		List<String> list = Arrays.asList("a", "b", "c");

		assertEquals(0, Colls.indexOf(list, item -> item.equals("a")));
		assertEquals(1, Colls.indexOf(list, item -> item.equals("b")));
		assertEquals(2, Colls.indexOf(list, item -> item.equals("c")));
		assertEquals(-1, Colls.indexOf(list, item -> item.equals("d")));
	}

}
