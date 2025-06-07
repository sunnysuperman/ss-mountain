package com.sunnysuperman.mountain.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.base.ComponentManager;
import com.sunnysuperman.mountain.base.locale.ErrorLocaleBundle;
import com.sunnysuperman.mountain.file.FileHelper;
import com.sunnysuperman.mountain.lang.utils.IOUtil;

public abstract class ExportExcelTask<T extends ExportJob<?>> implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(ExportExcelTask.class);

	private T job;
	private ExportJobRepository<T> jobRepository;
	private FileHelper fileHelper;
	private Sheet sheet;
	private int rowIndex = 0;

	protected ExportExcelTask(T job, ExportJobRepository<T> jobRepository) {
		super();
		this.job = job;
		this.jobRepository = jobRepository;
		fileHelper = ComponentManager.get(FileHelper.class);
	}

	protected T getJob() {
		return job;
	}

	protected abstract String[] getColumns();

	protected abstract String getFileName();

	protected abstract void writeData();

	protected abstract String upload(File file);

	protected int getBatchNum() {
		return 500;
	}

	@Override
	public final void run() {
		// 开始
		startJob();
		// 生成表格
		File file = null;
		try (SXSSFWorkbook wb = new SXSSFWorkbook(getBatchNum())) {
			writeToWorkbook(wb);
			// 创建文件
			file = fileHelper.createTmpFile("xlsx");
			// 写入文件
			writeWorkbookToFile(wb, file);
			// 上传前先报告一下状态
			setProgress((byte) 95);
			// 上传
			String outputUrl = upload(file);
			// 报告成功
			reportSuccess(outputUrl);
		} catch (Exception ex) {
			LOG.error(null, ex);
			reportFail(ErrorLocaleBundle.getErrorMsg(ex));
		} finally {
			IOUtil.deleteFileQuietly(file);
		}
	}

	private static void writeWorkbookToFile(Workbook wb, File file) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file)) {
			wb.write(out);
			out.flush();
		}
	}

	protected void writeToWorkbook(Workbook wb) {
		sheet = wb.createSheet();
		// 写入标题
		int cellIndex = -1;
		Row row = sheet.createRow(rowIndex);
		String[] columns = getColumns();
		for (int i = 0; i < columns.length; i++) {
			row.createCell(++cellIndex).setCellValue(columns[i]);
		}
		writeData();
	}

	protected final Row createRow() {
		return sheet.createRow(++rowIndex);
	}

	protected final byte getProgress() {
		return job.getProgress();
	}

	protected final void setProgress(int progress) {
		try {
			if (job.updateProgress(progress)) {
				jobRepository.update(job);
			}
		} catch (Exception ex) {
			LOG.error(null, ex);
		}
	}

	protected final void startJob() {
		try {
			job.start();
			jobRepository.update(job);
		} catch (Exception ex) {
			LOG.error(null, ex);
		}
	}

	protected final void increaseProgress(int progress) {
		setProgress(job.getProgress().byteValue() + progress);
	}

	protected final void reportSuccess(String outputUrl) {
		try {
			job.updateAsSuccess(outputUrl);
			jobRepository.update(job);
		} catch (Exception ex) {
			LOG.error(null, ex);
		}
	}

	protected final void reportFail(String errorMsg) {
		try {
			job.updateAsError(errorMsg);
			jobRepository.update(job);
		} catch (Exception ex) {
			LOG.error(null, ex);
		}
	}

}
