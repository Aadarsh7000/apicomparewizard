// Excel Write code 
package com.yash.apicomparewizard;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

public class ExcelWriter {
	private Workbook workbook;
	private String filePath;

	public ExcelWriter(String filePath) throws IOException {
		this.filePath = filePath;
		this.workbook = new XSSFWorkbook();
	}

	public void writeToExcel(List<String> mismatches, long timeTakenOld, long timeTakenNew, String sheetName) throws IOException {
	    Sheet sheet = workbook.createSheet(sheetName);

	    Font titleFont = workbook.createFont();
	    titleFont.setBold(true);

	    CellStyle titleStyle = workbook.createCellStyle();
	    titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
	    titleStyle.setFont(titleFont);

	    Font headerFont = workbook.createFont();
	    headerFont.setBold(true);

	    CellStyle headerStyle = workbook.createCellStyle();
	    headerStyle.setFont(headerFont);
	    headerStyle.setBorderBottom(BorderStyle.THIN);
	    headerStyle.setBorderTop(BorderStyle.THIN);
	    headerStyle.setBorderLeft(BorderStyle.THIN);
	    headerStyle.setBorderRight(BorderStyle.THIN);
	    headerStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
	    headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

	    CellStyle dataStyle = workbook.createCellStyle();
	    dataStyle.setBorderBottom(BorderStyle.THIN);
	    dataStyle.setBorderTop(BorderStyle.THIN);
	    dataStyle.setBorderLeft(BorderStyle.THIN);
	    dataStyle.setBorderRight(BorderStyle.THIN);
	    dataStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER); 

	    Row titleRow = sheet.createRow(0);
	    Cell titleCell = titleRow.createCell(0);
	    titleCell.setCellValue("Comparison Report");
	    titleCell.setCellStyle(titleStyle);
	    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3)); 

	    Row headerRow = sheet.createRow(1);
	    headerRow.createCell(0).setCellValue("Serial No");
	    headerRow.getCell(0).setCellStyle(headerStyle);

	    headerRow.createCell(1).setCellValue("Mismatch Details");
	    headerRow.getCell(1).setCellStyle(headerStyle);

	    headerRow.createCell(2).setCellValue("Old API Response Time");
	    headerRow.getCell(2).setCellStyle(headerStyle);

	    headerRow.createCell(3).setCellValue("New API Response Time");
	    headerRow.getCell(3).setCellStyle(headerStyle);

	    // Fill in mismatches
	    int rowIndex = 2; // Start from the third row
	    for (String mismatch : mismatches) {
	        Row row = sheet.createRow(rowIndex++);
	        Cell cell1 = row.createCell(0);
	        cell1.setCellValue(rowIndex - 2); 
	        cell1.setCellStyle(dataStyle); 

	        Cell cell2 = row.createCell(1);
	        cell2.setCellValue(mismatch);
	        cell2.setCellStyle(dataStyle);
	    }

	    int numberOfMismatches = mismatches.size();
	    if (numberOfMismatches == 1) {
	        // If there's only one mismatch, write the response times in the same row
	        Row singleMismatchRow = sheet.getRow(2); // The row for the single mismatch
	        singleMismatchRow.createCell(2).setCellValue(timeTakenOld + " ms");
	        singleMismatchRow.createCell(3).setCellValue(timeTakenNew + " ms");
	        singleMismatchRow.getCell(2).setCellStyle(dataStyle);
	        singleMismatchRow.getCell(3).setCellStyle(dataStyle);
	    } else if (numberOfMismatches > 1) {
	        // If there are multiple mismatches, merge the response time cells
	        Row firstMismatchRow = sheet.getRow(2);
	        Cell oldApiCell = firstMismatchRow.createCell(2);
	        oldApiCell.setCellValue(timeTakenOld + " ms");
	        oldApiCell.setCellStyle(dataStyle);
	        sheet.addMergedRegion(new CellRangeAddress(2, 2 + numberOfMismatches - 1, 2, 2));

	        Cell newApiCell = firstMismatchRow.createCell(3);
	        newApiCell.setCellValue(timeTakenNew + " ms");
	        newApiCell.setCellStyle(dataStyle);
	        sheet.addMergedRegion(new CellRangeAddress(2, 2 + numberOfMismatches - 1, 3, 3));
	    }

	    // Write to file
	    try (FileOutputStream fos = new FileOutputStream(filePath)) {
	        workbook.write(fos);
	    }
	}
	public void close() throws IOException {
		workbook.close();
	}
}
