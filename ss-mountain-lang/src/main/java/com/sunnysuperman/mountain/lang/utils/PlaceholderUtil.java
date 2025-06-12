package com.sunnysuperman.mountain.lang.utils;

import java.util.Map;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public final class PlaceholderUtil {

	private PlaceholderUtil() {
	}

	public static interface CompileHandler {

		String compile(String key, Map<String, Object> context);

	}

	private static final CompileHandler DEFAULT_COMPILE_HANDLER = (key, context) -> {
		Object value = context.get(key);
		if (value == null) {
			return null;
		}
		return value.toString();
	};

	private static final CompileOptions DEFAULT_OPTIONS = new CompileOptions().setRetainKeyIfNull(false);

	public static class CompileOptions {
		private boolean retainKeyIfNull;
		private String startToken;
		private String endToken;

		public boolean isRetainKeyIfNull() {
			return retainKeyIfNull;
		}

		public CompileOptions setRetainKeyIfNull(boolean retainKeyIfNull) {
			this.retainKeyIfNull = retainKeyIfNull;
			return this;
		}

		public String getStartToken() {
			return startToken;
		}

		public CompileOptions setStartToken(String startToken) {
			this.startToken = startToken;
			return this;
		}

		public String getEndToken() {
			return endToken;
		}

		public CompileOptions setEndToken(String endToken) {
			this.endToken = endToken;
			return this;
		}

	}

	public static String replaceRegexKeywords(String s) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\\') {
				buf.append("\\\\");
			} else if (c == '$') {
				buf.append("\\$");
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	@SuppressWarnings("squid:S3776")
	public static final String compile(final String s, Map<String, Object> context, CompileOptions options,
			CompileHandler handler) {
		if (s == null) {
			return null;
		}
		options = Objs.or(options, DEFAULT_OPTIONS);
		handler = Objs.or(handler, DEFAULT_COMPILE_HANDLER);
		final String tokenStartChars = Str.parse(options.getStartToken(), "${");
		final String tokenEndChars = Str.parse(options.getEndToken(), "}");
		final boolean retainKeyIfNull = options.retainKeyIfNull;
		final int len = s.length();
		StringBuilder buf = new StringBuilder();
		int fromIndex = 0;
		int bracketStartIndex = 0;
		while ((bracketStartIndex = s.indexOf(tokenStartChars, fromIndex)) >= 0) {
			int keyStartIndex = bracketStartIndex + tokenStartChars.length();
			int bracketEndIndex = s.indexOf(tokenEndChars, keyStartIndex);
			if (bracketEndIndex < 0) {
				throw new UnexpectedException("No bracket end: " + s.substring(bracketStartIndex));
			}
			String key = s.substring(keyStartIndex, bracketEndIndex);
			if (key.isEmpty()) {
				throw new UnexpectedException("Empty key");
			}
			String value = handler.compile(key, context);
			if (bracketStartIndex > fromIndex) {
				// append head
				buf.append(s.substring(fromIndex, bracketStartIndex));
			}
			if (value == null) {
				if (retainKeyIfNull) {
					buf.append(tokenStartChars).append(key).append(tokenEndChars);
				}
			} else {
				buf.append(value);
			}
			fromIndex = bracketEndIndex + tokenEndChars.length();
		}
		if (fromIndex < len) {
			if (fromIndex == 0) {
				// not found any token
				return s;
			}
			// append tail
			buf.append(s.substring(fromIndex));
		}
		return buf.toString();
	}

	public static final String compile(String content, Map<String, Object> context, CompileOptions options) {
		return compile(content, context, options, null);
	}

	public static final String compile(String content, Map<String, Object> context) {
		return compile(content, context, null, null);
	}

}
