package com.sunnysuperman.mountain.lang.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.sunnysuperman.mountain.lang.exception.FormatException;

public final class Dates {
	public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();
	public static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String ISO8601DATE_WITH_MILLS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final DateTimeFormatter[] FORMATTERS = {
			// 1. 标准带时区格式（带冒号）
			DateTimeFormatter.ISO_OFFSET_DATE_TIME,

			// 2. 自定义时区格式处理器（处理各种时区变体）
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T')
					.append(DateTimeFormatter.ISO_LOCAL_TIME).appendPattern("[XXX][XX][X]") // 同时匹配 +08:00, +0800, +08
					.toFormatter(),

			// 3. 不带时区但带毫秒
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),

			// 4. 不带时区不带毫秒
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),

			// 5. 仅日期
			DateTimeFormatter.ISO_LOCAL_DATE };

	private Dates() {
	}

	public static final Calendar getDefaultCalendar() {
		return Calendar.getInstance();
	}

	public static final Calendar getDefaultCalendar(Date date) {
		Calendar cal = getDefaultCalendar();
		cal.setTime(date);
		return cal;
	}

	public static final Date addMillSeconds(Date date, long mills) {
		return new Date(date.getTime() + mills);
	}

	public static final Date addSeconds(Date date, int seconds) {
		Calendar cal = getDefaultCalendar(date);
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}

	public static final Date addMinutes(Date date, int minutes) {
		Calendar cal = getDefaultCalendar(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	public static final Date addHours(Date date, int hours) {
		Calendar cal = getDefaultCalendar(date);
		cal.add(Calendar.HOUR, hours);
		return cal.getTime();
	}

	public static final Date addDays(Date date, int days) {
		Calendar cal = getDefaultCalendar(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	public static final Date addMonth(Date date, int months) {
		Calendar cal = getDefaultCalendar(date);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}

	public static final Date addYear(Date date, int years) {
		Calendar cal = getDefaultCalendar(date);
		cal.add(Calendar.YEAR, years);
		return cal.getTime();
	}

	public static final Calendar clearDateTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	/** 通用格式化日期时间解析 **/
	public static final Date parseDate(String s, String format) throws FormatException {
		if (Str.isEmpty(s)) {
			return null;
		}
		try {
			return getFormat(format).parse(s);
		} catch (Exception e) {
			throw new FormatException("Bad date string: " + s + " for format-" + format, e);
		}
	}

	/** 智能日期时间解析 **/
	public static Date parseDate(Object d, TimeZone tz) throws FormatException {
		if (d == null) {
			return null;
		}
		if (d instanceof Date) {
			return (Date) d;
		}
		if (d instanceof Number) {
			return new Date(Num.parseLong(d));
		}
		String s = d.toString();
		if (Str.isNumeric(s)) {
			return new Date(Long.valueOf(s));
		}
		return parseISO8601Date(s, tz);
	}

	public static Date parseDate(Object d) throws FormatException {
		return parseDate(d, null);
	}

	public static Date parseISO8601Date(String s, TimeZone tz) throws FormatException {
		if (s == null || s.isEmpty()) {
			return null;
		}
		if (tz == null) {
			tz = DEFAULT_TIMEZONE;
		}
		for (DateTimeFormatter formatter : FORMATTERS) {
			try {
				TemporalAccessor parsed = formatter.parse(s);
				if (parsed.query(TemporalQueries.offset()) != null) {
					// 带时区的情况
					return Date.from(parsed.query(ZonedDateTime::from).toInstant());
				} else if (parsed.isSupported(ChronoField.HOUR_OF_DAY)) {
					// 不带时区但有时间
					return Date.from(LocalDateTime.from(parsed).atZone(tz.toZoneId()).toInstant());
				} else {
					// 仅日期
					return Date.from(LocalDate.from(parsed).atStartOfDay(tz.toZoneId()).toInstant());
				}
			} catch (Exception e) {
				// 忽略，尝试下一个格式
			}
		}
		throw new FormatException("Failed to parse date: " + s);
	}

	/** 只解析日期 **/
	public static final Date parseDateOnly(String s) throws FormatException {
		if (Str.isEmpty(s)) {
			return null;
		}
		if (Str.isNumeric(s)) {
			Calendar cal = clearDateTime(getDefaultCalendar());
			cal.setTimeInMillis(Long.parseLong(s));
			clearDateTime(cal);
			return cal.getTime();
		}
		if (s.length() != 10) {
			throw new FormatException("Bad date string: " + s);
		}
		return parseDate(s, DATE_FORMAT);
	}

	public static final String format(Date date, String format) {
		if (date == null) {
			return null;
		}
		return getFormat(format).format(date);
	}

	public static final String formatISO8601Date(Date date) {
		DateFormat format = new SimpleDateFormat(ISO8601DATE_WITH_MILLS_FORMAT);
		format.setTimeZone(GMT_TIMEZONE);
		return format.format(date);
	}

	public static final String formatDateOnly(Date date) {
		if (date == null) {
			return null;
		}
		return getFormat(DATE_FORMAT).format(date);
	}

	public static final SimpleDateFormat getFormat(String s) {
		SimpleDateFormat format = new SimpleDateFormat(s);
		format.setTimeZone(DEFAULT_TIMEZONE);
		return format;
	}

}
