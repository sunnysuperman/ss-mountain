package com.sunnysuperman.mountain.base.locale;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.IOUtil;
import com.sunnysuperman.mountain.lang.utils.Str;

public class FileSystemPropertiesLocaleBundle extends LocaleBundle {

	public static class FileSystemPropertiesLocaleBundleOptions extends LocaleBundleOptions {
		private File dir;
		private String resourcePrefix;
		private Charset charset;

		public File getDir() {
			return dir;
		}

		public void setDir(File dir) {
			this.dir = dir;
		}

		public String getResourcePrefix() {
			return resourcePrefix;
		}

		public void setResourcePrefix(String resourcePrefix) {
			this.resourcePrefix = resourcePrefix;
		}

		public Charset getCharset() {
			return charset;
		}

		public void setCharset(Charset charset) {
			this.charset = charset;
		}

	}

	public FileSystemPropertiesLocaleBundle(FileSystemPropertiesLocaleBundleOptions options) {
		super(options);
		File dir = options.getDir();
		String resourcePrefix = options.getResourcePrefix();
		Stream.of(dir.listFiles()).forEach(file -> {
			if (!file.isFile()) {
				return;
			}
			if (resourcePrefix != null && !file.getName().startsWith(resourcePrefix)) {
				return;
			}
			String locale = detectLocaleFromFileName(file.getName(), resourcePrefix, options.getDefaultLocale());
			Map<String, String> props = null;
			try {
				props = IOUtil.readProperties(new FileInputStream(file), options.getCharset(), false);
			} catch (Exception e) {
				throw new UnexpectedException("Failed to load: " + file.getAbsolutePath());
			}
			for (Entry<String, String> entry : props.entrySet()) {
				String key = Str.trimToNull(entry.getKey());
				String value = Str.trimToNull(entry.getValue());
				if (key == null || value == null) {
					continue;
				}
				put(key, locale, value);
			}
		});
		finishPut();
	}

	public static String detectLocaleFromFileName(String fileName, String prefix, String defaultLocale) {
		String locale = tryToDetectLocaleFromFileName(fileName, prefix);
		if (locale == null) {
			locale = defaultLocale;
		}
		if (Str.isEmpty(locale)) {
			throw new UnexpectedException("Bad locale fileName: " + fileName);
		}
		return locale;
	}

	private static String tryToDetectLocaleFromFileName(String fileName, String prefix) {
		int end = fileName.indexOf('.');
		if (end <= 0) {
			return Str.EMPTY;
		}
		if (prefix == null) {
			return fileName.substring(0, end);
		}
		int start = fileName.indexOf(prefix);
		if (start != 0) {
			return Str.EMPTY;
		}
		start = prefix.length();
		if (end < start) {
			return Str.EMPTY;
		}
		if (end == start) {
			return null;
		}
		return fileName.substring(start + 1, end);
	}

}
