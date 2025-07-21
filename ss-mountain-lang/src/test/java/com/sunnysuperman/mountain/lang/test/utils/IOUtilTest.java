package com.sunnysuperman.mountain.lang.test.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sunnysuperman.mountain.lang.utils.IOUtil;

class IOUtilTest {

	@TempDir
	File tempDir;

	@Test
	void testCopy() throws IOException {
		String testData = "Hello, World!";
		InputStream input = new ByteArrayInputStream(testData.getBytes());
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IOUtil.copy(input, output);
		assertEquals(testData, output.toString());
	}

	@Test
	void testCopyWithBufferSize() throws IOException {
		String testData = "Hello, World!";
		InputStream input = new ByteArrayInputStream(testData.getBytes());
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IOUtil.copy(input, output, 1024);
		assertEquals(testData, output.toString());
	}

	@Test
	void testReadByteArrayFromFile() throws IOException {
		File testFile = new File(tempDir, "test.txt");
		String content = "Test file content";
		Files.write(testFile.toPath(), content.getBytes());

		byte[] result = IOUtil.readByteArray(testFile);
		assertArrayEquals(content.getBytes(), result);
	}

	@Test
	void testReadByteArrayFromInputStream() throws IOException {
		String content = "Test input stream content";
		InputStream input = new ByteArrayInputStream(content.getBytes());

		byte[] result = IOUtil.readByteArray(input);
		assertArrayEquals(content.getBytes(), result);
	}

	@Test
	void testReadStringFromFileWithCharset() throws IOException {
		File testFile = new File(tempDir, "test.txt");
		String content = "Test file content with charset";
		Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

		String result = IOUtil.readString(testFile, StandardCharsets.UTF_8);
		assertEquals(content, result);
	}

	@Test
	void testReadStringFromFile() throws IOException {
		File testFile = new File(tempDir, "test.txt");
		String content = "Test file content default charset";
		Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

		String result = IOUtil.readString(testFile);
		assertEquals(content, result);
	}

	@Test
	void testReadStringFromInputStreamWithCharset() throws IOException {
		String content = "Test input stream with charset";
		InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

		String result = IOUtil.readString(input, StandardCharsets.UTF_8);
		assertEquals(content, result);
	}

	@Test
	void testReadStringFromInputStream() throws IOException {
		String content = "Test input stream default charset";
		InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

		String result = IOUtil.readString(input);
		assertEquals(content, result);
	}

	@Test
	void testReadWithLineHandler() throws IOException {
		String content = "Line 1\nLine 2\nLine 3";
		InputStream input = new ByteArrayInputStream(content.getBytes());

		StringBuilder result = new StringBuilder();
		IOUtil.read(input, StandardCharsets.UTF_8, (s, line) -> {
			result.append("Line ").append(line).append(": ").append(s).append("\n");
			return true;
		});

		String expected = "Line 1: Line 1\nLine 2: Line 2\nLine 3: Line 3\n";
		assertEquals(expected, result.toString());
	}

	@Test
	void testReadProperties() throws IOException {
		String propertiesContent = "key1=value1\nkey2=value2\nkey3=value3";
		InputStream input = new ByteArrayInputStream(propertiesContent.getBytes());

		Map<String, String> result = IOUtil.readProperties(input, StandardCharsets.UTF_8, false);
		assertEquals(3, result.size());
		assertEquals("value1", result.get("key1"));
		assertEquals("value2", result.get("key2"));
		assertEquals("value3", result.get("key3"));
	}

	@Test
	void testGetFileExtension() {
		assertNull(IOUtil.getFileExtension(null));
		assertNull(IOUtil.getFileExtension(""));
		assertNull(IOUtil.getFileExtension("file"));
		assertNull(IOUtil.getFileExtension("file."));
		assertEquals("txt", IOUtil.getFileExtension("file.txt"));
		assertEquals("jpg", IOUtil.getFileExtension("image.jpg"));
		assertEquals("pdf", IOUtil.getFileExtension("document.pdf"));
		assertEquals("gz", IOUtil.getFileExtension("archive.tar.gz"));
	}

	@Test
	void testDeleteFile() throws IOException {
		File testFile = new File(tempDir, "test.txt");
		Files.write(testFile.toPath(), "test content".getBytes());

		assertTrue(IOUtil.deleteFile(testFile));
		assertFalse(testFile.exists());
	}

	@Test
	void testDeleteFileQuietly() throws IOException {
		File testFile = new File(tempDir, "test.txt");
		Files.write(testFile.toPath(), "test content".getBytes());

		IOUtil.deleteFileQuietly(testFile);
		assertFalse(testFile.exists());

		// Should not throw exception for null
		IOUtil.deleteFileQuietly(null);
	}

	@Test
	void testCreateFile() throws IOException {
		File testFile = new File(tempDir, "newfile.txt");

		assertTrue(IOUtil.createFile(testFile));
		assertTrue(testFile.exists());

		// Second call should return false as file already exists
		assertFalse(IOUtil.createFile(testFile));
	}

}