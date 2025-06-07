package com.sunnysuperman.mountain.lang.test.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.pagination.PageQuery;
import com.sunnysuperman.mountain.lang.pagination.PullPageQuery;

class PageQueryTest {

	@Test
	void testPage() {
		PageQuery q = new PageQuery();

		try {
			q.validate();
			assertTrue(false);
		} catch (ValidationException ex) {
			assertEquals("page", ex.getMessage());
		}

		q.forPage(10);
		q.validate();

		q.forPage(100);
		q.validate();

		q.forPage(101);
		try {
			q.validate();
			assertTrue(false);
		} catch (ValidationException ex) {
			assertEquals("page.limit", ex.getMessage());
		}
	}

	@Test
	void testPullPage() {
		PullPageQuery q = new PullPageQuery();

		try {
			q.validate();
			assertTrue(false);
		} catch (ValidationException ex) {
			assertEquals("pullPage", ex.getMessage());
		}

		q.forPullPage(10);
		q.validate();

		q.forPullPage(100);
		q.validate();

		q.forPullPage(101);
		try {
			q.validate();
			assertTrue(false);
		} catch (ValidationException ex) {
			assertEquals("pullPage.limit", ex.getMessage());
		}
	}

}
