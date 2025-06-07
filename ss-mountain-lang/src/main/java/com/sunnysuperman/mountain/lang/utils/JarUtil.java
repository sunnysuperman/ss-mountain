package com.sunnysuperman.mountain.lang.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class JarUtil {

	private JarUtil() {
	}

	public static interface ClassPathFileHandler {

		boolean willOpenStream(String fileName, String fullPath, boolean isDirectory) throws IOException;

		void streamOpened(String fileName, String fullPath, InputStream in) throws IOException;

	}

	public static void listClassPathFiles(Class<?> clazz, String dirPath, ClassPathFileHandler handler)
			throws IOException {
		URL dirURL = clazz.getResource(dirPath);
		if (dirURL == null) {
			throw new IOException("Bad dirPath: " + dirPath);
		}
		String protocol = dirURL.getProtocol();
		if (protocol.equals("file")) {
			File file = new File(dirURL.getFile());
			if (!file.isDirectory()) {
				throw new IOException("Not a directory");
			}
			listSystemPathFiles(file, file.getAbsolutePath(), handler);
		} else if (protocol.equals("jar")) {
			// file:/xxx/xxx.jar!/conf/locales
			String dirInJarPath = dirURL.getPath();
			String jarKey = ".jar!";
			int jarOffset = dirInJarPath.indexOf(jarKey);
			String jarPath = dirInJarPath.substring(5, jarOffset + jarKey.length() - 1);
			dirPath = dirInJarPath.substring(jarOffset + jarKey.length());
			listClassPathFiles(jarPath, dirPath, handler);
		} else {
			throw new IOException("Bad dirPath: " + dirPath);
		}
	}

	public static void listClassPathFiles(String jarPath, String dirPath, ClassPathFileHandler handler)
			throws IOException {
		JarFile jar = new JarFile(jarPath);
		dirPath = IOUtil.toUnixFilePath(dirPath);
		char slash = '/';
		try {
			Enumeration<JarEntry> entries = jar.entries();
			String relativePath = dirPath;
			// conf/locales
			if (!relativePath.isEmpty()) {
				if (relativePath.charAt(0) == slash) {
					relativePath = dirPath.substring(1);
				}
				if (!relativePath.isEmpty() && relativePath.charAt(relativePath.length() - 1) != slash) {
					relativePath += slash;
				}
			}
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				handleJarEntry(jar, entry, relativePath, handler);
			}
		} finally {
			IOUtil.close(jar);
		}
	}

	private static void handleJarEntry(JarFile jar, JarEntry entry, String relativePath, ClassPathFileHandler handler)
			throws IOException {
		String fullEntryName = IOUtil.toUnixFilePath(entry.getName());
		if (fullEntryName == null || fullEntryName.isEmpty()) {
			return;
		}
		String entryName = fullEntryName;
		if (!relativePath.isEmpty()) {
			if (!fullEntryName.startsWith(relativePath) || fullEntryName.length() == relativePath.length()) {
				return;
			}
			entryName = fullEntryName.substring(relativePath.length());
		}
		char slash = '/';
		if (entryName.charAt(entryName.length() - 1) == slash) {
			entryName = entryName.substring(0, entryName.length() - 1);
		}
		int slashOffset = entryName.lastIndexOf(slash);
		String simpleName = entryName;
		if (slashOffset > 0) {
			simpleName = entryName.substring(slashOffset + 1);
		}
		if (!handler.willOpenStream(simpleName, entryName, entry.isDirectory())) {
			return;
		}
		if (!entry.isDirectory()) {
			handler.streamOpened(simpleName, entryName, jar.getInputStream(entry));
		}
	}

	private static void listSystemPathFiles(final File dir, final String basePath, final ClassPathFileHandler handler)
			throws IOException {
		int headLen = basePath.length();
		// case1 / /a -> a
		// case2 /b /b/c -> c
		if (headLen > 1) {
			headLen++;
		}
		for (File file : dir.listFiles()) {
			String relativePath = IOUtil.toUnixFilePath(file.getAbsolutePath().substring(headLen));
			if (handler.willOpenStream(file.getName(), relativePath, file.isDirectory())) {
				if (file.isDirectory()) {
					listSystemPathFiles(file, basePath, handler);
				} else {
					handler.streamOpened(file.getName(), relativePath, new FileInputStream(file));
				}
			}
		}
	}
}
