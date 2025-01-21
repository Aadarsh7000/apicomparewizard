package com.yash.apicomparewizard;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApiComparisonUtility {
    public static void main(String[] args) throws Exception {
        ConfigLoader config = new ConfigLoader("src/main/resources/config.properties");
        String method = config.getProperty("method");
        String requestBodyPath = config.getProperty("request_body_path");
        String oldUrl = config.getProperty("old_api_url");
        String newUrl = config.getProperty("new_api_url");
        String outputExcel = config.getProperty("output_excel_path");
        String uniqueField = config.getProperty("uniqueField");
        List<String> fieldsToCompare = Arrays.asList(config.getArrayProperty("fields_to_compare", ","));

        Map<String, String> oldPathVariables = parsePathVariables(config.getProperty("old_api_path_variables"));
        Map<String, String> newPathVariables = parsePathVariables(config.getProperty("new_api_path_variables"));
        Map<String, String> sharedPathVariables = parsePathVariables(config.getProperty("shared_path_variables"));

        Map<String, String> oldQueryParams = parseQueryParams(config.getProperty("old_api_query_params"));
        Map<String, String> newQueryParams = parseQueryParams(config.getProperty("new_api_query_params"));
        Map<String, String> sharedQueryParams = parseQueryParams(config.getProperty("shared_query_params"));

        String finalOldUrl = UrlBuilder.buildUrl(oldUrl, oldPathVariables, sharedPathVariables, oldQueryParams, sharedQueryParams);
        String finalNewUrl = UrlBuilder.buildUrl(newUrl, newPathVariables, sharedPathVariables, newQueryParams, sharedQueryParams);
        
        System.out.println("finalOldUrl==>>" + finalOldUrl);
        System.out.println("finalNewUrl==>>" + finalNewUrl);

        ExcelWriter excelWriter = new ExcelWriter(outputExcel);
        File folder = new File(requestBodyPath);
        File[] jsonFiles = Optional.ofNullable(folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"))).orElse(new File[0]);

        if (jsonFiles.length != 0 && method.equalsIgnoreCase("post")) {
            for (File jsonFile : jsonFiles) {
                String requestBody = new String(Files.readAllBytes(jsonFile.toPath()));
                System.out.println("requestBody: " + requestBody);
                compareApiResponses(finalOldUrl, finalNewUrl, method, requestBody, uniqueField, excelWriter, jsonFile.getName());
            }
        } else {
            compareApiResponses(finalOldUrl, finalNewUrl, method, null, uniqueField, excelWriter, "Comparison_Output");
        }
        excelWriter.close();
    }

    private static void compareApiResponses(String oldUrl, String newUrl, String method, String requestBody, String uniqueField, ExcelWriter excelWriter, String sheetName) throws Exception {
        long timeTakenOld = measureResponseTime(() -> ApiRequestHandler.sendRequest(oldUrl, method, requestBody));
        long timeTakenNew = measureResponseTime(() -> ApiRequestHandler.sendRequest(newUrl, method, requestBody));

        String oldResponse = ApiRequestHandler.sendRequest(oldUrl, method, requestBody);
        String newResponse = ApiRequestHandler.sendRequest(newUrl, method, requestBody);
        
        List<Map<String, String>> mismatches = ResponseComparator.compareResponses(oldResponse, newResponse, uniqueField);
        excelWriter.writeToExcel(mismatches, timeTakenOld, timeTakenNew, sheetName);
    }

    private static long measureResponseTime(ResponseSupplier supplier) throws Exception {
        long startTime = System.currentTimeMillis();
        supplier.get();
        return System.currentTimeMillis() - startTime;
    }

    private static Map<String, String> parsePathVariables(String pathVariables) {
        return Optional.ofNullable(pathVariables)
                .filter(var -> !var.trim().isEmpty())
                .map(var -> Arrays.stream(var.split(","))
                        .map(v -> v.split("="))
                        .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1])))
                .orElse(Map.of());
    }

    private static Map<String, String> parseQueryParams(String queryParams) {
        return Optional.ofNullable(queryParams)
                .filter(param -> !param.trim().isEmpty())
                .map(param -> Arrays.stream(param.split(","))
                        .map(p -> p.split("="))
                        .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1])))
                .orElse(Map.of());
    }

    @FunctionalInterface
    private interface ResponseSupplier {
        String get() throws Exception;
    }
}