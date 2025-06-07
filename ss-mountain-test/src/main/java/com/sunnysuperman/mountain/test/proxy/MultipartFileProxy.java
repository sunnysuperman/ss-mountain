package com.sunnysuperman.mountain.test.proxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

/** 文件上传类代理 **/
public class MultipartFileProxy implements MultipartFile {

	File file;

	public MultipartFileProxy(File file) {
		this.file = file;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getOriginalFilename() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return new byte[0];
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		throw new UnsupportedOperationException("transferTo");
	}

	public File getFile() {
		return file;
	}
}