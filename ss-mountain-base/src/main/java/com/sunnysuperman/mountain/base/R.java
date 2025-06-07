package com.sunnysuperman.mountain.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.exception.Exceptions;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.IOUtil;

public class R {
	private static final Logger LOG = LoggerFactory.getLogger(R.class);

	private R() {
	}

	public static interface StreamReader {

		void read(InputStream in);

	}

	public static URL getResource(String path) {
		return R.class.getResource(getResourceClassPath(path));
	}

	public static InputStream getStream(String path) {
		return R.class.getResourceAsStream(getResourceClassPath(path));
	}

	public static String getString(String path) {
		try {
			return IOUtil.readString(getStream(path));
		} catch (IOException ex) {
			throw new UnexpectedException(ex);
		}
	}

	public static byte[] getBytes(String path) {
		try {
			return IOUtil.readByteArray(getStream(path));
		} catch (IOException ex) {
			throw new UnexpectedException(ex);
		}
	}

	public static boolean read(String path, StreamReader reader) {
		String resourcePath = getResourceClassPath(path).substring(1);
		Enumeration<URL> resources;
		try {
			resources = Thread.currentThread().getContextClassLoader().getResources(resourcePath);
			if (!resources.hasMoreElements()) {
				resources = ClassLoader.getSystemClassLoader().getResources(resourcePath);
				if (!resources.hasMoreElements()) {
					LOG.warn("No resources of '{}'", resourcePath);
					return false;
				} else {
					LOG.warn("Get resources by system loader instead of context loader: {}", resourcePath);
				}
			}
		} catch (IOException e) {
			throw new UnexpectedException("Failed to getResources by path: " + resourcePath, e);
		}
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			InputStream in;
			try {
				in = resource.openStream();
			} catch (IOException e) {
				throw new UnexpectedException("Failed to openStream by resource: " + resource, e);
			}
			try {
				reader.read(in);
			} catch (Exception e) {
				throw Exceptions.wrapRuntimeException(e);
			} finally {
				IOUtil.close(in);
			}
		}
		return true;
	}

	private static String getResourceClassPath(String path) {
		return "/res/" + path;
	}

}
