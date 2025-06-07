package com.sunnysuperman.mountain.lang.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.sunnysuperman.mountain.lang.exception.FormatException;

public final class Dates {
	public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();
	public static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
	public static final String ISO8601DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String ISO8601DATE_WITH_MILLS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String ISO8601DATE_WITH_MILLS_TIMEZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String ISO8601DATE_WITH_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
	public static final String ISO8601DATE_WITH_ZONE_MILLS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final int ISO8601DATE_FORMAT_VALUE_LENGTH = ISO8601DATE_FORMAT.length() - 4;

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

	public static Date parseISO8601Date(String s, TimeZone tz) throws FormatException {
		if (s == null || s.isEmpty()) {
			return null;
		}
		try {
			Date date;
			if (s.charAt(s.length() - 1) == 'Z') {
				String format = (s.length() == ISO8601DATE_FORMAT_VALUE_LENGTH) ? ISO8601DATE_FORMAT
						: ISO8601DATE_WITH_MILLS_FORMAT;
				DateFormat dateFormat = new SimpleDateFormat(format);
				dateFormat.setTimeZone(GMT_TIMEZONE);
				date = dateFormat.parse(s);
			} else if (s.length() == DATE_FORMAT.length()) {
				DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
				dateFormat.setTimeZone(tz != null ? tz : TimeZone.getDefault());
				date = dateFormat.parse(s);
			} else if (s.indexOf('.') >= 0) {
				date = new SimpleDateFormat(ISO8601DATE_WITH_ZONE_MILLS_FORMAT).parse(s);
			} else if (s.indexOf('T') >= 0) {
				date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(s);
			} else {
				date = new SimpleDateFormat(ISO8601DATE_WITH_ZONE_FORMAT).parse(s);
			}
			return date;
		} catch (Exception e) {
			throw new FormatException("Failed to parseISO8601Date: " + s, e);
		}
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
