package com.sunnysuperman.mountain.base.locale;

public class LocaleBundleOptions {
	boolean strictMode;
	private String defaultLocale;
	private String[] prefLocales;
	private String logKey;
	boolean escapeSpecialChars = true;
	private String compileStartToken;
	private String compileEndToken;

	public boolean isStrictMode() {
		return strictMode;
	}

	public LocaleBundleOptions setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
		return this;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public LocaleBundleOptions setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
		return this;
	}

	public String[] getPrefLocales() {
		return prefLocales;
	}

	public LocaleBundleOptions setPrefLocales(String[] prefLocales) {
		this.prefLocales = prefLocales;
		return this;
	}

	public String getLogKey() {
		return logKey;
	}

	public LocaleBundleOptions setLogKey(String logKey) {
		this.logKey = logKey;
		return this;
	}

	public boolean isEscapeSpecialChars() {
		return escapeSpecialChars;
	}

	public LocaleBundleOptions setEscapeSpecialChars(boolean escapeSpecialChars) {
		this.escapeSpecialChars = escapeSpecialChars;
		return this;
	}

	public String getCompileStartToken() {
		return compileStartToken;
	}

	public LocaleBundleOptions setCompileStartToken(String compileStartToken) {
		this.compileStartToken = compileStartToken;
		return this;
	}

	public String getCompileEndToken() {
		return compileEndToken;
	}

	public LocaleBundleOptions setCompileEndToken(String compileEndToken) {
		this.compileEndToken = compileEndToken;
		return this;
	}

}