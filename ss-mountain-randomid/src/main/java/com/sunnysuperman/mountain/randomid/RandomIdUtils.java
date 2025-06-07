package com.sunnysuperman.mountain.randomid;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.sunnysuperman.mountain.lang.utils.Dates;

public class RandomIdUtils {
	private static final Random RANDOM = new Random();

	private RandomIdUtils() {
	}

	public static String concat(String segment, String random) {
		int segmentLen = segment.length();
		int randomLen = random.length();
		int len = segmentLen + randomLen;
		String s1 = segmentLen < randomLen ? random : segment;
		String s2 = segmentLen >= randomLen ? random : segment;

		StringBuilder buf = new StringBuilder(len);
		int s1AppendedLen = 0;
		int s2AppendedLen = 0;
		for (int i = 0; i < len; i++) {
			if (s1AppendedLen < s1.length() && (i % 2 == 0 || s2AppendedLen >= s2.length())) {
				buf.append(s1.charAt(s1AppendedLen));
				s1AppendedLen++;
				continue;
			}
			buf.append(s2.charAt(s2AppendedLen));
			s2AppendedLen++;
		}

		return buf.toString();
	}

	public static int[] randomArrayOfMax(int n) {
		int[] x = new int[n];
		for (int i = 0; i < n; i++) {
			x[i] = i;
		}
		for (int i = 0; i < n; i++) {
			int in = RANDOM.nextInt(n - i) + i;
			int t = x[in];
			x[in] = x[i];
			x[i] = t;
		}
		return x;
	}

	public static Date getNextDayStart() {
		Calendar cal = Dates.getDefaultCalendar();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Dates.clearDateTime(cal);
		return cal.getTime();
	}

	public static int date2day(Date date) {
		Calendar cal = Dates.getDefaultCalendar();
		cal.setTime(date);
		return date2day(cal);
	}

	private static int date2day(Calendar cal) {
		return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
	}

}
