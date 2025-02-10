package com.yash.apicomparewizard;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ExcelReader {
    public static List<Map<String, String>> readApiDetails(File excelFile) throws Exception {
        List<Map<String, String>> apiDetails = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
               for (int rowIndex = 1; rowIndex <= sheet.getPhysicalNumberOfRows(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    break; 
                }
                Map<String, String> apiDetail = new HashMap<>();
                apiDetail.put("oldUrl", getCellValue(row.getCell(0)));
                apiDetail.put("newUrl", getCellValue(row.getCell(1)));
                apiDetail.put("method", getCellValue(row.getCell(2)));
                apiDetail.put("uniqueField", getCellValue(row.getCell(3)));
                apiDetail.put("oldHeader", getCellValue(row.getCell(4))); 
                apiDetail.put("newHeader", getCellValue(row.getCell(5)));
                apiDetail.put("requestBody", getCellValue(row.getCell(6))); 
                apiDetails.add(apiDetail);
            }
        }
        return apiDetails;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}