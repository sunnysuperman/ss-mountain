package com.sunnysuperman.mountain.lang.pagination;

import java.util.Date;

import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;

public class MarkerUtils {
	private static final String MIN_DATE_MARKER = "0";
	private static final String MAX_DATE_MARKER = "9999999999999";
	private static final String MIN_LONG_NUM_MARKER = Num.pad(1, 15, true);

	private MarkerUtils() {
	}

	public static int parseOffset(String s) {
		int offsetAsInt = Str.isEmpty(s) ? 0 : Integer.parseInt(s);
		return offsetAsInt < 0 ? 0 : offsetAsInt;
	}

	/** 转换时间到标记字串 **/
	public static String getDateMarker(Date date, Object id) {
		if (id == null) {
			throw new IllegalArgumentException("id");
		}
		return Num.pad(date.getTime(), 13, true) + "_" + id;
	}

	/** 转换时间到标记字串 **/
	public static String getDateMarker(Date date) {
		return Num.pad(date.getTime(), 13, true);
	}

	/** 转换开始时间标记字串 **/
	public static String getStartDateMarker(Date date) {
		long seconds = date.getTime() / 1000;
		return Num.pad(seconds * 1000L, 13, true);
	}

	/** 转换截止时间标记字串 **/
	public static String getEndDateMarker(Date date) {
		long seconds = date.getTime() / 1000;
		if (seconds % 60 == 59) {
			seconds++;
		}
		return Num.pad(seconds * 1000L, 13, true);
	}

	public static String getMinDateMarker() {
		return MIN_DATE_MARKER;
	}

	public static String getMaxDateMarker() {
		return MAX_DATE_MARKER;
	}

	/** 转换数字到标记字串 **/
	public static String getLongNumMarker(long num, Object uid) {
		return Num.pad(num, 15, true) + "_" + uid;
	}

	public static String getMinLongNumMarker() {
		return MIN_LONG_NUM_MARKER;
	}

	public static Date parseDateFromMarker(String marker) {
		int offset = marker.indexOf('_');
		long timestamp = Long.parseLong(offset < 0 ? marker : marker.substring(0, offset));
		return new Date(timestamp);
	}
}
