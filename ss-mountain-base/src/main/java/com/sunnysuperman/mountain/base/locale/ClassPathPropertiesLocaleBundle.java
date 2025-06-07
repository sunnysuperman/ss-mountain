package com.sunnysuperman.mountain.base.locale;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import com.sunnysuperman.mountain.lang.exception.Exceptions;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.IOUtil;
import com.sunnysuperman.mountain.lang.utils.JarUtil;
import com.sunnysuperman.mountain.lang.utils.JarUtil.ClassPathFileHandler;
import com.sunnysuperman.mountain.lang.utils.Str;

public class ClassPathPropertiesLocaleBundle extends LocaleBundle {

	public static class ClassPathPropertiesLocaleBundleOptions extends LocaleBundleOptions {
		private Class<?> clazz;
		private String path;
		private String resourcePrefix;
		private Charset charset;

		public Class<?> getClazz() {
			return clazz;
		}

		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
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

	public ClassPathPropertiesLocaleBundle(final ClassPathPropertiesLocaleBundleOptions options) {
		super(options);
		try {
			JarUtil.listClassPathFiles(
					options.getClazz() == null ? ClassPathPropertiesLocaleBundle.class : options.getClazz(),
					options.getPath(), new MyClassPathFileHandler(options));
		} catch (Exception e) {
			throw Exceptions.wrapRuntimeException(e);
		}
		finishPut();
	}

	private class MyClassPathFileHandler implements ClassPathFileHandler {
		ClassPathPropertiesLocaleBundleOptions options;

		public MyClassPathFileHandler(ClassPathPropertiesLocaleBundleOptions options) {
			super();
			this.options = options;
		}

		@Override
		public boolean willOpenStream(String fileName, String fullPath, boolean isDirectory) throws IOException {
			if (isDirectory) {
				return true;
			}
			return options.getResourcePrefix() == null || fileName.startsWith(options.getResourcePrefix());
		}

		@Override
		public void streamOpened(String fileName, String fullPath, InputStream in) throws IOException {
			String locale = FileSystemPropertiesLocaleBundle.detectLocaleFromFileName(fileName,
					options.getResourcePrefix(), options.getDefaultLocale());
			Map<String, String> props = null;
			try {
				props = IOUtil.readProperties(in, options.getCharset(), false);
			} catch (Exception e) {
				throw new UnexpectedException("Failed to load: " + fileName);
			}
			for (Entry<String, String> entry : props.entrySet()) {
				String key = Str.trimToNull(entry.getKey());
				String value = Str.trimToNull(entry.getValue());
				if (key == null || value == null) {
					continue;
				}
				put(key, locale, value);
			}
		}

	}

}
