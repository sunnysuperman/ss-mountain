package com.sunnysuperman.mountain.file;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.sunnysuperman.mountain.lang.exception.Exceptions;
import com.sunnysuperman.mountain.lang.id.ObjectIdGenerator;
import com.sunnysuperman.mountain.lang.id.ObjectIdGeneratorFactory;
import com.sunnysuperman.mountain.lang.utils.IOUtil;
import com.sunnysuperman.mountain.lang.utils.Str;

public class FileHelper {
	private static final Logger LOG = LoggerFactory.getLogger(FileHelper.class);
	private static final ObjectIdGenerator ID_GENERATOR = ObjectIdGeneratorFactory.create();

	private FileProperties fileProperties;
	private Environment environment;
	private String tmpDir;

	public FileHelper(FileProperties fileProperties, Environment environment) {
		super();
		this.fileProperties = fileProperties;
		this.environment = environment;
	}

	@PostConstruct
	public void init() {
		tmpDir = fileProperties.getTmpDir();
		if (Str.isEmpty(tmpDir)) {
			tmpDir = environment.getProperty("spring.servlet.multipart.location");
		}
		if (Str.isEmpty(tmpDir)) {
			tmpDir = System.getProperty("java.io.tmpdir");
		}
		if (Str.isEmpty(tmpDir)) {
			throw Exceptions.wrapRuntimeException("file.tmpDir is empty");
		}
		LOG.info(">>>>>>[file] tmp directory: {}", tmpDir);
	}

	private File makeTmpFile(boolean isDir, String extension) throws IOException {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		StringBuilder buf = new StringBuilder(tmpDir).append('/').append(year).append('/').append(month).append('/')
				.append(day).append('/').append(ID_GENERATOR.generate());
		if (Str.isNotEmpty(extension)) {
			buf.append('.').append(extension);
		}
		File file = new File(buf.toString());
		if (isDir) {
			if (!file.mkdirs()) {
				throw new IOException("Failed to create file/dir");
			}
		} else {
			IOUtil.createFile(file);
		}
		return file;
	}

	public File createTmpFile(String extension) throws IOException {
		return makeTmpFile(false, extension);
	}

	public File createTmpDir() throws IOException {
		return makeTmpFile(true, null);
	}

	public String getFileExtension(String fileName) {
		return IOUtil.getFileExtension(fileName);
	}

}
