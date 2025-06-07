package com.sunnysuperman.mountain.lang.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public final class IOUtil {
	private static final Logger LOG = LoggerFactory.getLogger(IOUtil.class);
	public static final String LINE = System.getProperty("line.separator");

	private IOUtil() {
	}

	/**
	 * 关闭流
	 */
	public static void close(Closeable in) {
		if (in != null) {
			try {
				in.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/**
	 * 拷贝输入流到输出流，每次读写8K
	 */
	public static void copy(InputStream input, OutputStream output) throws IOException {
		copy(input, output, 8024);
	}

	/**
	 * 拷贝输入流到输出流
	 */
	public static void copy(InputStream input, OutputStream output, int buffersize) throws IOException {
		byte[] buf = new byte[buffersize];
		int n;
		while ((n = input.read(buf)) != -1) {
			if (n > 0) {
				output.write(buf, 0, n);
			}
		}
	}

	/**
	 * 从文件中读取字节数组
	 */
	public static byte[] readByteArray(File file) throws IOException {
		return Files.readAllBytes(file.toPath());
	}

	/**
	 * 从输入流中读取字节数组
	 */
	public static byte[] readByteArray(InputStream in) throws IOException {
		return in.readAllBytes();
	}

	/**
	 * 从文件中读取文本内容
	 */
	public static String readString(File file, Charset charset) throws IOException {
		return Files.readString(file.toPath(), Objs.or(charset, StandardCharsets.UTF_8));
	}

	/**
	 * 从文件中读取文本内容
	 */
	public static String readString(File file) throws IOException {
		return readString(file, null);
	}

	/**
	 * 从输入流中读取文本内容
	 */
	public static String readString(InputStream in, Charset charset) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream is = in) {
			byte[] buffer = new byte[8192];
			int length;
			while ((length = is.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			return out.toString(Objs.or(charset, StandardCharsets.UTF_8).name());
		}
	}

	/**
	 * 从输入流中读取文本内容
	 */
	public static String readString(InputStream in) throws IOException {
		return readString(in, null);
	}

	/**
	 * 逐行读取字符串处理器
	 */
	public static interface ReadLineHandler {

		/**
		 * 处理每行的字符串
		 * 
		 * @param s    当前行的字符串
		 * @param line 当前行(第一行为1)
		 * @return 是否处理成功
		 */
		boolean handle(String s, int line);

	}

	/**
	 * 逐行读取字符串
	 */
	public static void read(InputStream in, Charset charset, ReadLineHandler handler) throws IOException {
		String s;
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(in, Objs.or(charset, StandardCharsets.UTF_8)))) {
			int i = 0;
			while ((s = reader.readLine()) != null) {
				if (!handler.handle(s, ++i)) {
					break;
				}
			}
		}
	}

	private static class ReadPropertiesLineHandler implements ReadLineHandler {
		private String concatKey = null;
		private String concatStr = null;
		private LinkedHashMap<String, String> properties = new LinkedHashMap<>();
		private boolean escape;

		public ReadPropertiesLineHandler(boolean escape) {
			super();
			this.escape = escape;
		}

		public String[] getKV(String s) {
			int offset = s.indexOf('=');
			if (offset <= 0) {
				throw new UnexpectedException("Bad config line: " + s);
			}
			String key = s.substring(0, offset).trim();
			if (key.isEmpty()) {
				throw new UnexpectedException("Empty key");
			}
			String value = s.substring(offset + 1).trim();
			return new String[] { key, value };
		}

		@SuppressWarnings("squid:S3516")
		@Override
		public boolean handle(String s, int line) {
			s = s.trim();
			if (concatStr == null) {
				if (s.isEmpty() || s.charAt(0) == '#') {
					return true;
				}
			} else {
				if (s.isEmpty()) {
					properties.put(concatKey, concatStr);
					concatStr = null;
					return true;
				}
			}
			s = Str.parseUnicode(s);
			if (escape) {
				s = Str.escape(s);
			}
			if (s.charAt(s.length() - 1) == '\\') {
				s = s.substring(0, s.length() - 1);
				if (concatStr == null) {
					String[] kv = getKV(s);
					concatKey = kv[0];
					concatStr = kv[1];
				} else {
					concatStr += s;
				}
				return true;
			} else if (concatStr != null) {
				concatStr += s;
				properties.put(concatKey, concatStr);
				concatStr = null;
				return true;
			}
			String[] kv = getKV(s);
			properties.put(kv[0], kv[1]);
			return true;
		}

		public Map<String, String> getProperties() {
			return properties;
		}

	}

	public static Map<String, String> readProperties(InputStream in, Charset charset, boolean escape)
			throws IOException {
		ReadPropertiesLineHandler handler = new ReadPropertiesLineHandler(escape);
		read(in, charset, handler);
		return handler.getProperties();
	}

	/**
	 * 获取文件扩展名
	 */
	public static String getFileExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		fileName = fileName.trim();
		int index = fileName.lastIndexOf('.');
		if (index > 0 && index < fileName.length() - 1) {
			return fileName.substring(index + 1).toLowerCase();
		}
		return null;
	}

	/**
	 * 删除文件
	 */
	public static boolean deleteFile(File file) throws IOException {
		if (!file.exists()) {
			return false;
		}
		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				deleteFile(subFile);
			}
		}
		Files.delete(file.toPath());
		return true;
	}

	/**
	 * 删除文件(静默)
	 */
	public static void deleteFileQuietly(File file) {
		if (file == null) {
			return;
		}
		try {
			deleteFile(file);
		} catch (Exception e) {
			LOG.error(null, e);
		}
	}

	/**
	 * 创建文件
	 */
	public static boolean createFile(File file) throws IOException {
		if (file.exists()) {
			return false;
		}
		File parent = file.getParentFile();
		if (parent != null) {
			parent.mkdirs();
		}
		return file.createNewFile();
	}

	/**
	 * 转换成类Unix系统文件路径
	 */
	public static String toUnixFilePath(String s) {
		if (File.separatorChar == '/') {
			return s;
		}
		return s.replace("\\", "/");
	}

}
