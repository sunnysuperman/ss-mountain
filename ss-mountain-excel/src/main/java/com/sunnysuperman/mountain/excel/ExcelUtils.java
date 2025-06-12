package com.sunnysuperman.mountain.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.monitorjbl.xlsx.StreamingReader;
import com.sunnysuperman.mountain.lang.utils.Dates;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;

public class ExcelUtils {
	private ExcelUtils() {
	}

	public static void writeToFile(Workbook wb, File file) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file)) {
			wb.write(out);
			out.flush();
		} finally {
			wb.close();
		}
	}

	public static Workbook loadWorkbook(File file) throws ExcelException {
		return loadWorkbook(file, 0);
	}

	public static Workbook loadWorkbook(File file, int rowCacheSize) throws ExcelException {
		try (InputStream in = new FileInputStream(file)) {
			return loadWorkbook(in, rowCacheSize);
		} catch (Exception e) {
			throw new ExcelException(ExcelException.ERROR_NOT_AN_EXCEL_FILE);
		}
	}

	public static Workbook loadWorkbook(InputStream in, int rowCacheSize) throws ExcelException {
		if (rowCacheSize > 0) {
			return StreamingReader.builder().rowCacheSize(rowCacheSize).bufferSize(4096).open(in);
		}
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(in);
		} catch (Exception e) {
			throw new ExcelException(ExcelException.ERROR_NOT_AN_EXCEL_FILE);
		}
		return wb;
	}

	public static Workbook newWorkbook(int rowCacheSize) {
		return new SXSSFWorkbook(rowCacheSize);
	}

	public static Workbook newWorkbook() {
		return new XSSFWorkbook();
	}

	public static Sheet ensureSheet(Workbook wb, int index) {
		if (index < wb.getNumberOfSheets()) {
			return wb.getSheetAt(index);
		}
		return wb.createSheet();
	}

	public static Row ensureRow(Sheet sheet, int rowNum) {
		Row row = sheet.getRow(rowNum);
		if (row == null) {
			row = sheet.createRow(rowNum);
		}
		return row;
	}

	public static void copyRow(Row srcRow, Row destRow) {
		for (int i = srcRow.getFirstCellNum(); i <= srcRow.getLastCellNum(); i++) {
			Cell srcCell = srcRow.getCell(i);
			if (srcCell != null) {
				copyCell(srcCell, destRow.createCell(i));
			}
		}
	}

	public static Cell ensureCell(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		if (cell == null) {
			cell = row.createCell(cellIndex);
		}
		return cell;
	}

	public static void copyCell(Cell src, Cell dest) {
		if (src == null) {
			return;
		}
		switch (src.getCellType()) {
		case STRING:
			dest.setCellValue(src.getStringCellValue());
			break;
		case NUMERIC:
			dest.setCellValue(src.getNumericCellValue());
			break;
		case BOOLEAN:
			dest.setCellValue(src.getBooleanCellValue());
			break;
		case FORMULA:
			dest.setCellFormula(src.getCellFormula());
			break;
		case BLANK:
			dest.setBlank();
			break;
		default:
			break;
		}
	}

	public static Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			return cell.getNumericCellValue();
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case FORMULA:
			return cell.getCellFormula();
		default:
			return null;
		}
	}

	public static Object getCellValue(Cell cell, ExcelCellType hintType) throws ExcelException {
		switch (hintType) {
		case STRING:
			return getStringCellValue(cell);
		case INT:
			return getIntCellValue(cell);
		case FLOAT:
			return getFloatCellValue(cell);
		case DOUBLE:
			return getDoubleCellValue(cell);
		case LONG:
			return getLongCellValue(cell);
		case DATE:
			return getDateCellValue(cell);
		default:
			throw new ExcelException(ExcelException.ERROR_UNKNOWN_CELL_TYPE, hintType);
		}
	}

	public static String getStringCellValue(Cell cell) throws ExcelException {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case STRING: {
			return Str.trimToNull(cell.getStringCellValue());
		}
		case NUMERIC: {
			if (DateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				return new SimpleDateFormat(Dates.ISO8601DATE_WITH_MILLS_FORMAT).format(date);
			}
			double d = cell.getNumericCellValue();
			long l = (long) d;
			if (l == d) {
				return String.valueOf(l);
			} else {
				return String.valueOf(d);
			}
		}
		case BLANK:
			return null;
		default:
			throw new ExcelException(ExcelException.ERROR_CELL_TYPE_NOT_MATCH, cell.getCellType());
		}
	}

	public static Number getNumericCellValue(Cell cell) throws ExcelException {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case NUMERIC:
			return cell.getNumericCellValue();
		case STRING:
			return Num.parseNumber(Str.trimToNull(cell.getStringCellValue()));
		case BLANK:
			return null;
		default:
			throw new ExcelException(ExcelException.ERROR_CELL_TYPE_NOT_MATCH, cell.getCellType());
		}
	}

	public static int getIntCellValue(Cell cell) throws ExcelException {
		Number number = getNumericCellValue(cell);
		return number == null ? 0 : number.intValue();
	}

	public static float getFloatCellValue(Cell cell) throws ExcelException {
		Number number = getNumericCellValue(cell);
		return number == null ? 0f : number.floatValue();
	}

	public static double getDoubleCellValue(Cell cell) throws ExcelException {
		Number number = getNumericCellValue(cell);
		return number == null ? 0d : number.doubleValue();
	}

	public static long getLongCellValue(Cell cell) throws ExcelException {
		Number number = getNumericCellValue(cell);
		return number == null ? 0L : number.longValue();
	}

	public static Date getDateCellValue(Cell cell) throws ExcelException {
		if (cell == null) {
			return null;
		}
		if (DateUtil.isCellDateFormatted(cell)) {
			return cell.getDateCellValue();
		}
		switch (cell.getCellType()) {
		case STRING:
			try {
				return parseDateFromString(cell.getStringCellValue());
			} catch (Exception e) {
				throw new ExcelException(ExcelException.ERROR_CELL_TYPE_NOT_MATCH, CellType.STRING);
			}
		case BLANK:
			return null;
		default:
			break;
		}
		throw new ExcelException(ExcelException.ERROR_CELL_TYPE_NOT_MATCH, cell.getCellType());
	}

	private static Date parseDateFromString(String s) {
		if (Str.isBlank(s)) {
			return null;
		}
		StringBuilder buf = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			// skip bad characters
			if (c > 0 && c < 128) {
				buf.append(c);
			}
		}
		s = buf.toString();
		if (Str.isNumeric(s)) {
			return new Date(Long.valueOf(s));
		}
		return Dates.parseISO8601Date(s, TimeZone.getDefault());
	}

	public static void setCellValue(Cell cell, Object value, ExcelCellType hintType) throws ExcelException {
		if (value == null) {
			return;
		}
		switch (hintType) {
		case STRING:
			cell.setCellValue(Str.parse(value));
			break;
		case INT:
			cell.setCellValue(Num.parseInteger(value));
			break;
		case FLOAT:
			cell.setCellValue(Num.parseFloat(value));
			break;
		case DOUBLE:
			cell.setCellValue(Num.parseDouble(value));
			break;
		case LONG:
			cell.setCellValue(Num.parseLong(value));
			break;
		default:
			throw new ExcelException(ExcelException.ERROR_UNKNOWN_CELL_TYPE, hintType);
		}
	}

	public static void setCellValue(Cell cell, Object value) {
		if (value == null) {
			return;
		}
		if (value instanceof String) {
			cell.setCellValue((String) value);
		} else if (value instanceof Number) {
			cell.setCellValue(((Number) value).doubleValue());
		} else if (value instanceof Date) {
			cell.setCellValue(((Date) value));
		} else if (value instanceof Boolean) {
			cell.setCellValue(((Boolean) value));
		} else {
			cell.setCellValue(value.toString());
		}
	}

}
