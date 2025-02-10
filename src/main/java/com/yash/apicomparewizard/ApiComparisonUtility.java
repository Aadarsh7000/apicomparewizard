package com.yash.apicomparewizard;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;

public class ApiComparisonUtility {
    public static void main(String[] args) throws Exception {
    	
    	String excelPath=args[0];
    	String outputPath=args[1];
    	
       File excelFile = new File(excelPath);
       List<Map<String, String>> apiDetails = ExcelReader.readApiDetails(excelFile);
       int outputCounter = 1;

    	for (Map<String, String> apiDetail : apiDetails) {
    		    String requestBody = apiDetail.get("requestBody");
    	        String oldUrl = apiDetail.get("oldUrl");
    	        String newUrl = apiDetail.get("newUrl");
    	        String uniqueField = apiDetail.get("uniqueField");
    	        String oldHeaderString = apiDetail.get("oldHeader");
    	        String newHeaderString = apiDetail.get("newHeader");
    	        String method = apiDetail.get("method");

    	        
    	        Map<String, String> oldApiHeader = parseData(oldHeaderString);
    	        Map<String, String> newApiHeader = parseData(newHeaderString);
    	        
    	        
    	        HttpHeaders oldHeader=HeaderBuilder.buildHeader(oldApiHeader);
    	        HttpHeaders newHeader=HeaderBuilder.buildHeader(newApiHeader);
    	        String outputExcel = outputPath + outputCounter + ".xlsx";
    	        ExcelWriter excelWriter = new ExcelWriter(outputExcel);
    	       	 
    	        if (method.equalsIgnoreCase("post") || method.equalsIgnoreCase("put")) {
    	        	    System.out.println("requestBody: " + requestBody);
    	                compareApiResponses(oldUrl, newUrl, method, requestBody, uniqueField, excelWriter, "Comparison_Output",oldHeader,newHeader);
    	            }
    	         else {
    	            compareApiResponses(oldUrl, newUrl, method, null, uniqueField, excelWriter, "Comparison_Output",oldHeader,newHeader);
    	        }
    	        excelWriter.close();
    	        outputCounter++;
      	        
    	}

    }
 
    private static void compareApiResponses(String oldUrl, String newUrl, String method, String requestBody, String uniqueField, ExcelWriter excelWriter, String sheetName,HttpHeaders oldHeader,HttpHeaders newHeader) throws Exception {
 

        long timeTakenMilliStart = (new Date()).getTime();
        String oldResponse = ApiRequestHandler.sendRequest(oldUrl, method, requestBody,oldHeader);
        long timeTakenOld = (new Date()).getTime() - timeTakenMilliStart;
        timeTakenMilliStart = (new Date()).getTime();
        String newResponse = ApiRequestHandler.sendRequest(newUrl, method, requestBody,newHeader);
        long timeTakenNew = (new Date()).getTime() - timeTakenMilliStart;
 
        List<Map<String, String>> mismatches = ResponseComparator.compareResponses(oldResponse, newResponse, uniqueField );
        excelWriter.writeToExcel(mismatches, timeTakenOld, timeTakenNew, sheetName);
    }
 
    private static Map<String, String> parseData(String data) {
        return Optional.ofNullable(data)
                .filter(var -> !var.trim().isEmpty())
                .map(var -> Arrays.stream(var.split(","))
                        .map(v -> v.split("="))
                        .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1])))
                .orElse(Map.of());
    }
 
}