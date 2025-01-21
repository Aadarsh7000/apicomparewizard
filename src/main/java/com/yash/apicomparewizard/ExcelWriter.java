package com.yash.apicomparewizard;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

	public void writeToExcel(List<Map<String,String>> mismatches, long timeTakenOld, long timeTakenNew, String sheetName) throws IOException {
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
        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
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

	    headerRow.createCell(1).setCellValue("Unique Field");
	    headerRow.getCell(1).setCellStyle(headerStyle);

	    headerRow.createCell(2).setCellValue("Mismatch Field");
	    headerRow.getCell(2).setCellStyle(headerStyle);
	    
	    headerRow.createCell(3).setCellValue("Old Value");
	    headerRow.getCell(3).setCellStyle(headerStyle);
	    
	    headerRow.createCell(4).setCellValue("New Value");
	    headerRow.getCell(4).setCellStyle(headerStyle);

	    headerRow.createCell(5).setCellValue("Old API Response Time");
	    headerRow.getCell(5).setCellStyle(headerStyle);

	    headerRow.createCell(6).setCellValue("New API Response Time");
	    headerRow.getCell(6).setCellStyle(headerStyle);

	    int rowIndex = 2; 
	    for (Map<String, String> mismatch : mismatches) {
	        Row row = sheet.createRow(rowIndex++);
	        Cell cell1 = row.createCell(0);
	        cell1.setCellValue(rowIndex - 2); 
	        cell1.setCellStyle(dataStyle); 
	        for (Map.Entry<String, String> entry : mismatch.entrySet()) {
	            Cell cell2 = row.createCell(1);
	            cell2.setCellValue(entry.getKey()); 
	            cell2.setCellStyle(dataStyle);
	            String[] splitValues = splitCombinedString(entry.getValue());
	            Cell cell3 = row.createCell(2);
	            cell3.setCellValue(splitValues[0]); 
	            cell3.setCellStyle(dataStyle);
	            Cell cell4 = row.createCell(3);
	            cell4.setCellValue(splitValues[1].replace("*",",")); 
	            cell4.setCellStyle(dataStyle);
	            Cell cell5 = row.createCell(4);
	            cell5.setCellValue(splitValues[2].replace("*",",")); 
	            cell5.setCellStyle(dataStyle);
	        }
	    }

	    int numberOfMismatches = mismatches.size();
	    if (numberOfMismatches == 1) {
	        Row singleMismatchRow = sheet.getRow(2); 
	        singleMismatchRow.createCell(5).setCellValue(timeTakenOld + " ms");
	        singleMismatchRow.createCell(6).setCellValue(timeTakenNew + " ms");
	        singleMismatchRow.getCell(5).setCellStyle(dataStyle);
	        singleMismatchRow.getCell(6).setCellStyle(dataStyle);
	    } else if (numberOfMismatches > 1) {
	        Row firstMismatchRow = sheet.getRow(2);
	        Cell oldApiCell = firstMismatchRow.createCell(5);
	        oldApiCell.setCellValue(timeTakenOld + " ms");
	        oldApiCell.setCellStyle(dataStyle);
	        sheet.addMergedRegion(new CellRangeAddress(2, 2 + mismatches.size() - 1, 5, 5));

	        Cell newApiCell = firstMismatchRow.createCell(6);
	        newApiCell.setCellValue(timeTakenNew + " ms");
	        newApiCell.setCellStyle(dataStyle);
	        sheet.addMergedRegion(new CellRangeAddress(2, 2 + numberOfMismatches - 1, 6, 6));
	    }

	    // Write to file
	    try (FileOutputStream fos = new FileOutputStream(filePath)) {
	        workbook.write(fos);
	    }
	}
	public void close() throws IOException {
		workbook.close();
	}
	
	public static String[] splitCombinedString(String combinedString) {
        return combinedString.split(",");
    }
}
