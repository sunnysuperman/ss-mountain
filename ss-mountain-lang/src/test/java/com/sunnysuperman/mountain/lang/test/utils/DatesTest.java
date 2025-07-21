package com.sunnysuperman.mountain.lang.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.utils.Dates;
import com.sunnysuperman.mountain.lang.utils.Str;

class DatesTest {

	@Test
	void testParseISO8601Date() {
		// 空
		assertNull(Dates.parseISO8601Date(null, null));
		assertNull(Dates.parseISO8601Date(Str.EMPTY, null));
		// 标准时区格式(带毫秒)
		{
			String dateStr = "2023-07-21T14:30:45.123Z";
			assertUTCTime(Dates.parseISO8601Date(dateStr, null), "2023-07-21T14:30:45.123Z");
		}
		// 标准时区格式(不带毫秒)
		{
			String dateStr = "2023-07-21T14:30:45Z";
			assertUTCTime(Dates.parseISO8601Date(dateStr, null), "2023-07-21T14:30:45.000Z");
		}
		// 带时区偏移的格式(带毫秒)
		{
			Map<String, String> map = Map.of("2023-07-21T14:30:45.123+08:00", "2023-07-21T06:30:45.123Z",
					"2023-07-21T02:00:45.123+0330", "2023-07-20T22:30:45.123Z", "2023-07-21T02:00:45.123+03",
					"2023-07-20T23:00:45.123Z", "2023-07-21T14:30:45.123-05:00", "2023-07-21T19:30:45.123Z");
			map.forEach((dateStr, utcDateStr) -> assertUTCTime(Dates.parseISO8601Date(dateStr, null), utcDateStr));
		}
		// 带时区偏移的格式(不带毫秒)
		{
			Map<String, String> map = Map.of("2023-07-21T14:30:45+08:00", "2023-07-21T06:30:45.000Z",
					"2023-07-21T02:00:45+0330", "2023-07-20T22:30:45.000Z", "2023-07-21T02:00:45+03",
					"2023-07-20T23:00:45.000Z", "2023-07-21T14:30:45-05:00", "2023-07-21T19:30:45.000Z");
			map.forEach((dateStr, utcDateStr) -> assertUTCTime(Dates.parseISO8601Date(dateStr, null), utcDateStr));
		}
		// 不带时区的日期时间格式(带毫秒)
		{
			String dateStr = "2023-07-21T14:30:45.123";
			assertUTCTime(Dates.parseISO8601Date(dateStr, null), "2023-07-21T06:30:45.123Z");
			assertUTCTime(Dates.parseISO8601Date(dateStr, TimeZone.getTimeZone("UTC")), "2023-07-21T14:30:45.123Z");
		}
		// 不带时区的日期时间格式(不带毫秒)
		{
			String dateStr = "2023-07-21T14:30:45";
			assertUTCTime(Dates.parseISO8601Date(dateStr, null), "2023-07-21T06:30:45.000Z");
			assertUTCTime(Dates.parseISO8601Date(dateStr, TimeZone.getTimeZone("UTC")), "2023-07-21T14:30:45.000Z");
		}
		// 纯日期
		{
			String dateStr = "2023-07-21";
			assertUTCTime(Dates.parseISO8601Date(dateStr, null), "2023-07-20T16:00:00.000Z");
			assertUTCTime(Dates.parseISO8601Date(dateStr, TimeZone.getTimeZone("UTC")), "2023-07-21T00:00:00.000Z");
		}
	}

	@Test
	void testFormatISO8601Date() {
		Date date = Dates.parseISO8601Date("2023-07-21T14:30:45.123Z", null);
		assertEquals("2023-07-21T14:30:45.123Z", Dates.formatISO8601Date(date));
	}

	@Test
	void testAddMillSeconds() {
		Date d1 = Dates.parseISO8601Date("2022-10-01T12:13:14+0800", TimeZone.getDefault());
		Date d2 = Dates.addMillSeconds(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-10-01T12:13:14.005+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddSeconds() {
		Date d1 = Dates.parseISO8601Date("2022-10-01T12:13:14+0800", TimeZone.getDefault());
		Date d2 = Dates.addSeconds(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-10-01T12:13:19+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddMinutes() {
		Date d1 = Dates.parseISO8601Date("2022-10-01T12:13:14+0800", TimeZone.getDefault());
		Date d2 = Dates.addMinutes(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-10-01T12:18:14+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddHours() {
		Date d1 = Dates.parseISO8601Date("2022-09-30T12:13:14+0800", TimeZone.getDefault());

		Date d2 = Dates.addHours(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-09-30T17:13:14+0800", TimeZone.getDefault()));

		Date d3 = Dates.addHours(d1, 30);
		assertEquals(d3, Dates.parseISO8601Date("2022-10-01T18:13:14+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddDays() {
		Date d1 = Dates.parseISO8601Date("2022-09-30T12:13:14+0800", TimeZone.getDefault());

		Date d2 = Dates.addDays(d1, 5);
		assertEquals(d2, Dates.parseISO8601Date("2022-10-05T12:13:14+0800", TimeZone.getDefault()));
	}

	@Test
	void testAddMonth() {
		Date d1 = Dates.parseISO8601Date("2022-01-31T12:13:14+0800", TimeZone.getDefault());

		Date d2 = Dates.addMonth(d1, 1);
		assertEquals(d2, Dates.parseISO8601Date("2022-02-28T12:13:14+0800", TimeZone.getDefault()));

		Date d3 = Dates.addMonth(d1, 2);
		assertEquals(d3, Dates.parseISO8601Date("2022-03-31T12:13:14+0800", TimeZone.getDefault()));

		Date d4 = Dates.addMonth(d1, 3);
		assertEquals(d4, Dates.parseISO8601Date("2022-04-30T12:13:14+0800", TimeZone.getDefault()));
	}

	private void assertUTCTime(Date date, String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		assertEquals(dateStr, sdf.format(date));
	}

}
