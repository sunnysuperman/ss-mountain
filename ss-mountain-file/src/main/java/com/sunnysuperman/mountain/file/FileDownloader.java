package com.sunnysuperman.mountain.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.sunnysuperman.mountain.httpclient.HttpClient;
import com.sunnysuperman.mountain.httpclient.HttpDownloadOptions;
import com.sunnysuperman.mountain.lang.utils.IOUtil;

public class FileDownloader {
	private FileHelper fileHelper;
	private HttpClient client;

	public FileDownloader(FileHelper fileHelper) {
		super();
		this.fileHelper = fileHelper;
	}

	@PostConstruct
	public void init() {
		client = new HttpClient(10, 30);
		client.setConnectTimeout(15);
		client.setReadTimeout(30);
	}

	public static class FileWrapper {
		File file;
		String contentType;

		public FileWrapper(File file, String contentType) {
			super();
			this.file = file;
			this.contentType = contentType;
		}

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

	}

	public File download(String url) throws IOException {
		return download(url, -1);
	}

	public File download(String url, long maxSize) throws IOException {
		FileWrapper wrapper = downloadAndGetContentType(url, maxSize);
		if (wrapper == null) {
			return null;
		}
		return wrapper.getFile();
	}

	public FileWrapper downloadAndGetContentType(String url, long maxSize) throws IOException {
		File file = fileHelper.createTmpFile(getExtensionByUrl(url));
		FileWrapper result = null;
		try (FileOutputStream out = new FileOutputStream(file)) {
			HttpDownloadOptions options = new HttpDownloadOptions();
			options.setRetrieveResponseHeaders(true);
			options.setMaxSize(maxSize);
			boolean downloaded = client.download(url, out, options);
			if (!downloaded) {
				throw new IOException("Failed to download: " + url);
			}
			Map<String, String> responseHeaders = options.getResponseHeaders();
			String contentType = responseHeaders.get("content-type");
			result = new FileWrapper(file, contentType);
			return result;
		} finally {
			if (result == null) {
				IOUtil.deleteFileQuietly(file);
			}
		}
	}

	public String getExtensionByUrl(String url) {
		String extension;
		int paramIndex = url.indexOf('?');
		if (paramIndex > 0) {
			extension = IOUtil.getFileExtension(url.substring(0, paramIndex));
		} else {
			extension = IOUtil.getFileExtension(url);
		}
		return extension;
	}

}
