package com.sunnysuperman.mountain.lang.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.utils.Dates;

class DatesTest {

	@Test
	void testAddMillSeconds() throws Exception {
		Date d1 = Dates.parseISO8601Date("2022-10-01T12:13:14+0800", TimeZone.getDefault());
		Date d2 = Dates.addMillSeconds(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-10-01T12:13:14.005+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddSeconds() throws Exception {
		Date d1 = Dates.parseISO8601Date("2022-10-01T12:13:14+0800", TimeZone.getDefault());
		Date d2 = Dates.addSeconds(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-10-01T12:13:19+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddMinutes() throws Exception {
		Date d1 = Dates.parseISO8601Date("2022-10-01T12:13:14+0800", TimeZone.getDefault());
		Date d2 = Dates.addMinutes(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-10-01T12:18:14+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddHours() throws Exception {
		Date d1 = Dates.parseISO8601Date("2022-09-30T12:13:14+0800", TimeZone.getDefault());

		Date d2 = Dates.addHours(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-09-30T17:13:14+0800", TimeZone.getDefault()));

		Date d3 = Dates.addHours(d1, 30);
		assertEquals(d3, Dates.parseISO8601Date("2022-10-01T18:13:14+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddDays() throws Exception {
		Date d1 = Dates.parseISO8601Date("2022-09-30T12:13:14+0800", TimeZone.getDefault());

		Date d2 = Dates.addDays(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-10-05T12:13:14+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddMonth() throws Exception {
		Date d1 = Dates.parseISO8601Date("2022-01-31T12:13:14+0800", TimeZone.getDefault());

		Date d2 = Dates.addMonth(d1, 1);
		assertEquals(d2, Dates.parseISO8601Date("2022-02-28T12:13:14+0800", TimeZone.getDefault()));

		Date d3 = Dates.addMonth(d1, 2);
		assertEquals(d3, Dates.parseISO8601Date("2022-03-31T12:13:14+0800", TimeZone.getDefault()));

		Date d4 = Dates.addMonth(d1, 3);
		assertEquals(d4, Dates.parseISO8601Date("2022-04-30T12:13:14+0800", TimeZone.getDefault()));
	}

}
