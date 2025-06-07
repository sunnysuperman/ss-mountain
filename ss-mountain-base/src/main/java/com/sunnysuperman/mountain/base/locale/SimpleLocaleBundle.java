package com.sunnysuperman.mountain.base.locale;

public class SimpleLocaleBundle extends LocaleBundle {

	public SimpleLocaleBundle(LocaleBundleOptions options) {
		super(options);
	}

	@Override
	public void put(String key, String locale, String value) {
		super.put(key, locale, value);
	}

	@Override
	public void finishPut() {
		super.finishPut();
	}

}