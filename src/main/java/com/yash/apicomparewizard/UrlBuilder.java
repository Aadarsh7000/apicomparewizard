package com.yash.apicomparewizard;

import java.util.Map;

public class UrlBuilder {
    public static String buildUrl(String baseUrl, Map<String, String> pathVariables, Map<String, String> sharedPathVariables, Map<String, String> queryParams, Map<String, String> sharedQueryParams) {
        for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
            baseUrl = baseUrl.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        for (Map.Entry<String, String> entry : sharedPathVariables.entrySet()) {
            baseUrl = baseUrl.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        StringBuilder queryBuilder = new StringBuilder();
        
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach((key, value) -> queryBuilder.append(key).append("=").append(value).append("&"));
        }
        
        if (sharedQueryParams != null && !sharedQueryParams.isEmpty()) {
            sharedQueryParams.forEach((key, value) -> queryBuilder.append(key).append("=").append(value).append("&"));
        }

        if (queryBuilder.length() > 0) {
            queryBuilder.setLength(queryBuilder.length() - 1);
            baseUrl += "?" + queryBuilder.toString();
        }

        return baseUrl;
    }
}