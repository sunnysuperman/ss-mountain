package com.sunnysuperman.mountain.excel.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.excel.ExcelException;
import com.sunnysuperman.mountain.excel.ExcelUtils;
import com.sunnysuperman.mountain.excel.reader.BatchHandler;
import com.sunnysuperman.mountain.excel.reader.DataAndRowIndex;
import com.sunnysuperman.mountain.excel.reader.ExcelColumn;
import com.sunnysuperman.mountain.excel.reader.ExcelReader;
import com.sunnysuperman.mountain.excel.reader.ExcelReaderOptions;
import com.sunnysuperman.mountain.excel.reader.HandlerException;
import com.sunnysuperman.mountain.lang.utils.IOUtil;
import com.sunnysuperman.mountain.lang.utils.Str;

class ExcelReaderTest {

	@Test
	void read() throws Exception {
		File file = makeSrcExcelFile(1000);
		AtomicInteger times = new AtomicInteger();
		AtomicInteger totalRecordsNum = new AtomicInteger();

		ExcelReaderOptions options = new ExcelReaderOptions().setStreaming(true)
				.setColumns(new ExcelColumn[] { new ExcelColumn("phone", "手机号") })
				.setHandler(new BatchHandler<DataAndRowIndex<String>>(300) {

					@Override
					protected DataAndRowIndex<String> parseData(ExcelReader reader, Map<String, Object> data,
							int rowIndex) throws HandlerException {
						return new DataAndRowIndex<>(data.get("phone").toString(), rowIndex);
					}

					@Override
					protected void handleBatch(ExcelReader reader, List<DataAndRowIndex<String>> dataList)
							throws HandlerException {
						if (times.incrementAndGet() <= 3) {
							assertEquals(300, dataList.size());
						} else {
							assertEquals(100, dataList.size());
						}
						totalRecordsNum.addAndGet(dataList.size());
					}

				});

		new ExcelReader(file, options).read();

		assertEquals(4, times.get());
		assertEquals(1000, totalRecordsNum.get());
	}

	@Test
	void readAndCopy() throws Exception {
		File file = makeSrcExcelFile(1000);

		int readRowCacheSize = 100;
		int writeRowCacheSize = 200;
		File feedbackFile = newFile("feedback");
		Workbook feedbackWorkbook = ExcelUtils.newWorkbook(writeRowCacheSize);
		Sheet feedbackSheet = ExcelUtils.ensureSheet(feedbackWorkbook, 0);

		ExcelReaderOptions options = new ExcelReaderOptions().setStreaming(true)
				.setStreamingRowCacheSize(readRowCacheSize)
				.setColumns(new ExcelColumn[] { new ExcelColumn("phone", "手机号") })
				.setHandler(new BatchHandler<DataAndRowIndex<String>>(writeRowCacheSize) {

					@Override
					public void onRowCopied(ExcelReader reader, Row row, Row copyRow, boolean isHeader)
							throws HandlerException {
						if (isHeader) {
							copyRow.createCell(1).setCellValue("校验结果");
							copyRow.createCell(2).setCellValue("原因");
						}
					}

					@Override
					protected DataAndRowIndex<String> parseData(ExcelReader reader, Map<String, Object> data,
							int rowIndex) throws HandlerException {
						String phone = Str.parse(data.get("phone"));
						boolean valid = phone != null && phone.length() == 11;
						return new DataAndRowIndex<>(valid ? phone : null, rowIndex);
					}

					@Override
					protected void handleBatch(ExcelReader reader, List<DataAndRowIndex<String>> dataList)
							throws HandlerException {
						for (DataAndRowIndex<String> data : dataList) {
							Row row = feedbackSheet.getRow(data.getRowIndex());
							String phone = data.getData();
							if (phone == null) {
								row.createCell(1).setCellValue("失败");
								row.createCell(2).setCellValue("手机号码格式错误");
							} else {
								row.createCell(1).setCellValue("成功");
							}
						}
					}

					@Override
					protected void end(ExcelReader reader) throws HandlerException {
						try {
							ExcelUtils.writeToFile(feedbackWorkbook, feedbackFile);
						} catch (IOException e) {
							throw new HandlerException(e);
						}
					}

				});

		new ExcelReader(file, options).setCopySheet(feedbackSheet).read();

		assertTrue(feedbackFile.length() > 0);
	}

	@Test
	void readAndCopy2() throws Exception {
		File file = makeSrcExcelFile(10000);
		File feedbackFile = newFile("feedback2");
		int writeRowCacheSize = 200;

		ExcelReaderOptions options = new ExcelReaderOptions().setStreaming(true)
				.setColumns(new ExcelColumn[] { new ExcelColumn("phone", "手机号") }).setCopy(true)
				.setHandler(new BatchHandler<DataAndRowIndex<String>>(writeRowCacheSize) {

					@Override
					public void onRowCopied(ExcelReader reader, Row row, Row copyRow, boolean isHeader)
							throws HandlerException {
						if (isHeader) {
							copyRow.createCell(1).setCellValue("校验结果");
							copyRow.createCell(2).setCellValue("原因");
						}
					}

					@Override
					protected DataAndRowIndex<String> parseData(ExcelReader reader, Map<String, Object> data,
							int rowIndex) throws HandlerException {
						String phone = Str.parse(data.get("phone"));
						boolean valid = phone != null && phone.length() == 11;
						return new DataAndRowIndex<>(valid ? phone : null, rowIndex);
					}

					@Override
					protected void handleBatch(ExcelReader reader, List<DataAndRowIndex<String>> dataList)
							throws HandlerException {
						for (DataAndRowIndex<String> data : dataList) {
							Row row = reader.getCopySheet().getRow(data.getRowIndex());
							String phone = data.getData();
							if (phone == null) {
								row.createCell(1).setCellValue("失败");
								row.createCell(2).setCellValue("手机号码格式错误");
							} else {
								row.createCell(1).setCellValue("成功");
							}
						}
					}

					@Override
					protected void end(ExcelReader reader) throws HandlerException {
						try {
							ExcelUtils.writeToFile(reader.getCopySheet().getWorkbook(), feedbackFile);
						} catch (IOException e) {
							throw new HandlerException(e);
						}
					}

				});

		new ExcelReader(file, options).read();

		assertTrue(feedbackFile.length() > 0);
		assertEquals(240, options.getCopyRowCacheSize());
	}

	@Test
	void readBadExcel() throws Exception {
		File file = makeSrcExcelFile(100);

		ExcelReaderOptions options = new ExcelReaderOptions().setStreaming(true)
				.setColumns(new ExcelColumn[] { new ExcelColumn("phone", "手机号2") }).setCopy(true)
				.setHandler(new BatchHandler<DataAndRowIndex<String>>(100) {

					@Override
					public void onRowCopied(ExcelReader reader, Row row, Row copyRow, boolean isHeader)
							throws HandlerException {
						// nope
					}

					@Override
					protected DataAndRowIndex<String> parseData(ExcelReader reader, Map<String, Object> data,
							int rowIndex) throws HandlerException {
						String phone = Str.parse(data.get("phone"));
						boolean valid = phone != null && phone.length() == 11;
						return new DataAndRowIndex<>(valid ? phone : null, rowIndex);
					}

					@Override
					protected void handleBatch(ExcelReader reader, List<DataAndRowIndex<String>> dataList)
							throws HandlerException {
						// nope
					}

					@Override
					protected void end(ExcelReader reader) throws HandlerException {
						// nope
					}

				});

		try {
			new ExcelReader(file, options).read();
			assertTrue(false);
		} catch (ExcelException e) {
			assertTrue(e.getErrorCode() > 0);
			System.out.println("不是符合模板的excel");
		}
	}

	@Test
	void handleError() throws Exception {
		File file = makeSrcExcelFile(100);

		ExcelReaderOptions options = new ExcelReaderOptions().setStreaming(true)
				.setColumns(new ExcelColumn[] { new ExcelColumn("phone", "手机号") }).setCopy(true)
				.setHandler(new BatchHandler<DataAndRowIndex<String>>(100) {

					@Override
					public void onRowCopied(ExcelReader reader, Row row, Row copyRow, boolean isHeader)
							throws HandlerException {
						// nope
					}

					@Override
					protected DataAndRowIndex<String> parseData(ExcelReader reader, Map<String, Object> data,
							int rowIndex) throws HandlerException {
						throw new HandlerException("处理数据失败");
					}

					@Override
					protected void handleBatch(ExcelReader reader, List<DataAndRowIndex<String>> dataList)
							throws HandlerException {
						// nope
					}

					@Override
					protected void end(ExcelReader reader) throws HandlerException {
						// nope
					}

				});

		try {
			new ExcelReader(file, options).read();
			assertTrue(false);
		} catch (HandlerException e) {
			assertEquals("处理数据失败", e.getMessage());
			System.out.println(e.getMessage());
		}
	}

	private File makeSrcExcelFile(int recordsNum) throws IOException {
		SXSSFWorkbook wb = new SXSSFWorkbook(300);
		SXSSFSheet sheet = wb.createSheet();
		int rowIndex = -1;

		Row titleRow = sheet.createRow(0);
		titleRow.createCell(++rowIndex).setCellValue("手机号");

		long start = 13800000000L;
		for (int i = 0; i < recordsNum; i++) {
			long phone = start + i;
			if (i % 6 == 0) {
				phone = phone * 10;
			}
			Row row = sheet.createRow(++rowIndex);
			row.createCell(0).setCellValue(String.valueOf(phone));
		}

		File file = newFile("src");
		ExcelUtils.writeToFile(wb, file);

		return file;
	}

	private File newFile(String name) throws IOException {
		File file = new File(new File(System.getProperty("user.dir")), "__tmp/" + name + ".xlsx");
		IOUtil.deleteFile(file);
		IOUtil.createFile(file);
		return file;
	}

}
