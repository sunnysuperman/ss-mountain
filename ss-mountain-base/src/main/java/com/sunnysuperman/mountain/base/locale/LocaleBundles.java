package com.sunnysuperman.mountain.base.locale;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.base.R;
import com.sunnysuperman.mountain.lang.exception.Exceptions;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.IOUtil;
import com.sunnysuperman.mountain.lang.utils.Str;

public class LocaleBundles {
	private static final Logger LOG = LoggerFactory.getLogger(LocaleBundles.class);
	public static final String LOCALE_ZH_CN = "zh_CN";
	private static final String DEFAULT_LOCALE;
	private static final SimpleLocaleBundle BUNDLE;

	static {
		URL indexRes = R.getResource("locale/index");
		if (indexRes == null) {
			DEFAULT_LOCALE = null;
			BUNDLE = null;
			LOG.info(">>>>>>[locale] no locales");
		} else {
			try {
				String[] locales = R.getString("locale/index").split(",");
				if (LOG.isInfoEnabled()) {
					LOG.info(">>>>>>[locale] locales: {}", Str.join(locales, ","));
				}
				DEFAULT_LOCALE = locales[0];

				LocaleBundleOptions options = new LocaleBundleOptions();
				options.setDefaultLocale(DEFAULT_LOCALE);
				options.setPrefLocales(locales);
				options.setCompileStartToken("{");
				options.setCompileEndToken("}");
				BUNDLE = new SimpleLocaleBundle(options);
				for (String l : locales) {
					String locale = l.trim();
					R.read("locale/" + locale + ".properties", in -> {
						Map<String, String> props;
						try {
							props = IOUtil.readProperties(in, StandardCharsets.UTF_8, false);
						} catch (IOException e) {
							throw Exceptions.wrapRuntimeException(e);
						}
						for (Entry<String, String> entry : props.entrySet()) {
							String key = Str.trimToNull(entry.getKey());
							String value = Str.trimToNull(entry.getValue());
							if (key == null || value == null) {
								continue;
							}
							BUNDLE.put(key, locale, value);
						}
					});
				}
				BUNDLE.finishPut();
			} catch (Exception e) {
				throw Exceptions.wrapRuntimeException(e);
			}
		}
	}

	private LocaleBundles() {
	}

	public static String getDefaultLocale() {
		return DEFAULT_LOCALE;
	}

	public static boolean containsKey(String key) {
		return ensureBundle().containsKey(key);
	}

	public static String get(String locale, String key) {
		if (locale == null) {
			locale = LOCALE_ZH_CN;
		}
		return ensureBundle().getWithParams(locale, key, null);
	}

	public static String getWithParams(String locale, String key, Map<String, Object> context) {
		if (locale == null) {
			locale = LOCALE_ZH_CN;
		}
		if (context == null) {
			context = Collections.emptyMap();
		}
		return ensureBundle().getWithParams(locale, key, context);
	}

	public static String getWithArrayParams(String locale, String key, Object[] params) {
		if (locale == null) {
			locale = LOCALE_ZH_CN;
		}
		return ensureBundle().getWithArrayParams(locale, key, params);
	}

	private static SimpleLocaleBundle ensureBundle() {
		if (BUNDLE == null) {
			throw new UnexpectedException("未初始化LocaleBundle，请确认locale/index文件是否存在");
		}
		return BUNDLE;
	}

}
