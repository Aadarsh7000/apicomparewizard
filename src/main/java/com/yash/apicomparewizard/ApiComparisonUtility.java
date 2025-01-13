// Main Class of the ApiComparisonUtility  
package com.yash.apicomparewizard;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiComparisonUtility {
	public static void main(String[] args) throws Exception {
		// Load configurations
		ConfigLoader config = new ConfigLoader("src/main/resources/config.properties");
		String method = config.getProperty("method");
		String requestBodyPath = config.getProperty("request_body_path");
		String oldUrl = config.getProperty("old_api_url");
		String newUrl = config.getProperty("new_api_url");
		String outputExcel = config.getProperty("output_excel_path");
		String[] fields = config.getArrayProperty("fields_to_compare", ",");
		List<String> fieldsToCompare = Arrays.asList(fields);
		// Load path variables and query parameters
		Map<String, String> oldPathVariables = parsePathVariables(config.getProperty("old_api_path_variables"));
		Map<String, String> newPathVariables = parsePathVariables(config.getProperty("new_api_path_variables"));
		Map<String, String> sharedPathVariables = parsePathVariables(config.getProperty("shared_path_variables"));

		Map<String, String> oldQueryParams = parseQueryParams(config.getProperty("old_api_query_params"));
		Map<String, String> newQueryParams = parseQueryParams(config.getProperty("new_api_query_params"));
		Map<String, String> sharedQueryParams = parseQueryParams(config.getProperty("shared_query_params"));

		// Request body

		// Build URLs
		String finalOldUrl = UrlBuilder.buildUrl(oldUrl, oldPathVariables, sharedPathVariables, oldQueryParams,
				sharedQueryParams);
		String finalNewUrl = UrlBuilder.buildUrl(newUrl, newPathVariables, sharedPathVariables, newQueryParams,
				sharedQueryParams);
		System.out.print("finalOldUrl==>>" + finalOldUrl);
		System.out.print("finalNewUrl==>>" + finalNewUrl);
		
		ExcelWriter excelWriter = new ExcelWriter(outputExcel);

		File folder = new File(requestBodyPath);
		File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
		if (jsonFiles.length !=0  && method =="post") {
			for (File jsonFile : jsonFiles) {
				String requestBody = new String(Files.readAllBytes(jsonFile.toPath()));
				System.out.println("requestBody"+ requestBody);
				long startTimeOld = System.currentTimeMillis();
				String oldResponse = ApiRequestHandler.sendRequest(finalOldUrl, method, requestBody);
				long endTimeOld = System.currentTimeMillis();
				long timeTakenOld = endTimeOld - startTimeOld;

				long startTimeNew = System.currentTimeMillis();
				String newResponse = ApiRequestHandler.sendRequest(finalNewUrl, method, requestBody);
				long endTimeNew = System.currentTimeMillis();
				long timeTakenNew = endTimeNew - startTimeNew;
				List<String> mismatches = ResponseComparator.compareResponses(oldResponse, newResponse, fieldsToCompare);
				String sheetName = jsonFile.getName().replace(".json", "");
				excelWriter.writeToExcel(mismatches, timeTakenOld, timeTakenNew,sheetName);
			}
		}else {

		long startTimeOld = System.currentTimeMillis();
		String oldResponse = ApiRequestHandler.sendRequest(finalOldUrl, method, null);
		long endTimeOld = System.currentTimeMillis();
		long timeTakenOld = endTimeOld - startTimeOld;

		long startTimeNew = System.currentTimeMillis();
		String newResponse = ApiRequestHandler.sendRequest(finalNewUrl, method, null);
		long endTimeNew = System.currentTimeMillis();
		long timeTakenNew = endTimeNew - startTimeNew;
		
		
		List<String> mismatches = ResponseComparator.compareResponses(oldResponse, newResponse, fieldsToCompare);
		excelWriter.writeToExcel(mismatches, timeTakenOld, timeTakenNew,"Comparison_Output");
		}
		excelWriter.close();		
	}

	private static Map<String, String> parsePathVariables(String pathVariables) {
		if (pathVariables == null || pathVariables.trim().isEmpty()) {
			return Map.of(); 
			}
		return Arrays.stream(pathVariables.split(",")).map(var -> var.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
	}

	private static Map<String, String> parseQueryParams(String queryParams) {
		if (queryParams == null || queryParams.trim().isEmpty()) {
			return Map.of(); 
			}
		return Arrays.stream(queryParams.split(",")).map(param -> param.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
	}
}

