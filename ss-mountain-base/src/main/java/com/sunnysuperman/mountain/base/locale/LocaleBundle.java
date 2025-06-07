package com.sunnysuperman.mountain.base.locale;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.PlaceholderUtil;
import com.sunnysuperman.mountain.lang.utils.PlaceholderUtil.CompileHandler;
import com.sunnysuperman.mountain.lang.utils.PlaceholderUtil.CompileOptions;
import com.sunnysuperman.mountain.lang.utils.Str;

public abstract class LocaleBundle {
	private final byte[] writeLock = new byte[0];
	private volatile boolean initialized = false;
	private Map<String, Map<String, String>> bundlesMap = new ConcurrentHashMap<>(0);
	protected final LocaleBundleOptions options;
	private final CompileOptions compileOptions;

	protected LocaleBundle(LocaleBundleOptions options) {
		this.options = options;
		this.compileOptions = new CompileOptions().setStartToken(options.getCompileStartToken())
				.setEndToken(options.getCompileEndToken());
		if (options.getDefaultLocale() == null) {
			throw new UnexpectedException(wrapLogMessage("No default locale set", options));
		}
	}

	protected String wrapLogMessage(String msg, LocaleBundleOptions options) {
		if (options.getLogKey() != null) {
			return options.getLogKey() + ": " + msg;
		}
		return msg;
	}

	protected void put(String key, String locale, String value) {
		if (Str.isEmpty(key)) {
			throw new IllegalArgumentException("Bad key");
		}
		if (Str.isEmpty(locale)) {
			throw new IllegalArgumentException("Bad locale");
		}
		value = Str.trimToNull(value);
		if (value == null) {
			throw new IllegalArgumentException("Bad value");
		}
		if (options.escapeSpecialChars) {
			value = Str.escape(value);
		}
		synchronized (writeLock) {
			Map<String, String> table = bundlesMap.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
			table.put(locale, value);
		}
	}

	protected void finishPut() {
		Set<String> locales = null;
		for (Entry<String, Map<String, String>> bundleEntry : bundlesMap.entrySet()) {
			String key = bundleEntry.getKey();
			Map<String, String> table = bundleEntry.getValue();
			if (table.get(options.getDefaultLocale()) == null) {
				throw new UnexpectedException(wrapLogMessage("No default value set for key: " + key, options));
			}
			if (options.strictMode) {
				if (locales == null) {
					locales = table.keySet();
				} else {
					Set<String> theLocales = table.keySet();
					if (theLocales.size() != locales.size() || !theLocales.containsAll(locales)) {
						throw new UnexpectedException(wrapLogMessage("Missing some locales for key: " + key, options));
					}
				}
			}
		}
		initialized = true;
	}

	public boolean containsKey(String key) {
		return bundlesMap.containsKey(key);
	}

	public int size() {
		return bundlesMap.size();
	}

	public String getRaw(String locale, String key) {
		if (!initialized) {
			throw new UnexpectedException("Does not finish init");
		}
		Map<String, String> table = bundlesMap.get(key);
		if (table == null) {
			return null;
		}
		if (locale != null) {
			locale = LocaleUtil.findSupportLocale(locale, table.keySet());
			if (locale != null) {
				return table.get(locale);
			}
		}
		String[] preferencedLocales = options.getPrefLocales();
		if (preferencedLocales != null) {
			for (String prefLocale : preferencedLocales) {
				String value = table.get(prefLocale);
				if (value != null) {
					return value;
				}
			}
		}
		return table.get(options.getDefaultLocale());
	}

	@Override
	public String toString() {
		return bundlesMap.toString();
	}

	public class LocaleCompileHandler implements CompileHandler {
		private String locale;

		public LocaleCompileHandler(String locale) {
			super();
			this.locale = locale;
		}

		private Number getNumber(String s, Map<String, Object> context) {
			return Str.isNumeric(s.charAt(0)) ? Num.parseNumber(s) : Num.parseNumber(context.get(s));
		}

		@Override
		public String compile(String key, Map<String, Object> context) {
			Object value = context.get(key);
			if (value != null) {
				return value.toString();
			}
			if (key.indexOf("subtract(") == 0) {
				String[] numbers = key.substring("subtract(".length(), key.indexOf(')')).split(",");
				Number n1 = getNumber(numbers[0], context);
				Number n2 = getNumber(numbers[1], context);
				if (n1 instanceof Double || n1 instanceof Float || n2 instanceof Double || n2 instanceof Float) {
					return String.valueOf(n1.doubleValue() - n2.doubleValue());
				}
				return String.valueOf(n1.longValue() - n2.longValue());
			}
			if (key.indexOf("plus(") == 0) {
				String[] numbers = key.substring("plus(".length(), key.indexOf(')')).split(",");
				Number n1 = getNumber(numbers[0], context);
				Number n2 = getNumber(numbers[1], context);
				if (n1 instanceof Double || n1 instanceof Float || n2 instanceof Double || n2 instanceof Float) {
					return String.valueOf(n1.doubleValue() + n2.doubleValue());
				}
				return String.valueOf(n1.longValue() + n2.longValue());
			}
			int arrayIndex = key.indexOf("[");
			if (arrayIndex > 0) {
				String prefix = key.substring(0, arrayIndex);
				String pluralKey = key.substring(arrayIndex + 1, key.indexOf(']'));
				Number number = Num.parseNumber(context.get(pluralKey));
				if (number == null) {
					return null;
				}
				String d = number.toString();
				String s = getWithParams(locale, prefix + "[" + d + "]", context);
				if (s != null) {
					return s;
				}
				return getWithParams(locale, prefix + "[other]", context);
			}
			return null;
		}

	}

	public String getWithParams(String locale, String key, Map<String, Object> context) {
		String text = getRaw(locale, key);
		if (context == null) {
			return text;
		}
		return PlaceholderUtil.compile(text, context, compileOptions, new LocaleCompileHandler(locale));
	}

	public String getWithArrayParams(String locale, String key, Object[] params) {
		String text = getRaw(locale, key);
		if (params == null) {
			return text;
		}
		Map<String, Object> context = new HashMap<>(params.length);
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			context.put(String.valueOf(i), param);
		}
		return PlaceholderUtil.compile(text, context, compileOptions, new LocaleCompileHandler(locale));
	}

}
