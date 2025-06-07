package com.sunnysuperman.mountain.web;

import com.sunnysuperman.mountain.lang.model.ApiResult;
import com.sunnysuperman.mountain.lang.utils.Bool;
import com.sunnysuperman.mountain.lang.utils.Num;

public abstract class AbstractController {

	public static final String SUCCESS_STR = "success";

	protected AbstractController() {
	}

	protected static ApiResult<String> outputSuccess() {
		return output(SUCCESS_STR);
	}

	protected static <T> ApiResult<T> output(T data) {
		return new ApiResult<>(0, data);
	}

	protected static int parseInt(Integer i) {
		return Num.parseIntValue(i, 0);
	}

	protected static int parseInt(Integer i, int defaultValue) {
		return Num.parseIntValue(i, defaultValue);
	}

	protected static boolean parseBoolean(String b) {
		return Bool.parseBoolValue(b, false);
	}

	protected static boolean parseBoolean(String b, boolean defaultValue) {
		return Bool.parseBoolValue(b, defaultValue);
	}

	protected static int parseLimit(Integer limit) {
		return parseLimit(limit, 100, 20);
	}

	protected static int parseLimit(Integer limit, int max, int defaults) {
		int val = Num.parseIntValue(limit, defaults);
		if (val <= 0 || val > max) {
			return defaults;
		}
		return val;
	}

}
