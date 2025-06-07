package com.sunnysuperman.mountain.job;

import java.util.regex.Pattern;

import com.sunnysuperman.mountain.lang.utils.Str;

public class JobUtils {

	private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z][a-z0-9-]*");

	private JobUtils() {
	}

	public static boolean isValidJobName(String jobName) {
		if (Str.isEmpty(jobName)) {
			return false;
		}
		return NAME_PATTERN.matcher(jobName).matches();
	}

	public static void checkJobName(String jobName) {
		if (!isValidJobName(jobName)) {
			throw new IllegalArgumentException("Bad job name: " + jobName + ", require: " + NAME_PATTERN);
		}
	}

}
