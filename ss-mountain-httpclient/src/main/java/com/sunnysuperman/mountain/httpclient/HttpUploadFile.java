package com.sunnysuperman.mountain.httpclient;

import java.io.File;

public class HttpUploadFile {
	private File file;
	private String fileName;
	private String contentType;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
