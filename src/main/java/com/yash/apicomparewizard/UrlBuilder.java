//package com.yash.apicomparewizard;
//
//import java.util.Map;
//public class UrlBuilder {
//   public static String buildUrl(String baseUrl, Map<String, String> pathVariables, Map<String, String> queryParams) {
//       for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
//           baseUrl = baseUrl.replace("{" + entry.getKey() + "}", entry.getValue());
//       }
//       if (queryParams != null && !queryParams.isEmpty()) {
//           StringBuilder query = new StringBuilder("?");
//           queryParams.forEach((key, value) -> query.append(key).append("=").append(value).append("&"));
//           baseUrl += query.substring(0, query.length() - 1);
//       }
//       return baseUrl;
//   }
//}
package com.yash.apicomparewizard;

import java.util.Map;

public class UrlBuilder {
    public static String buildUrl(String baseUrl, Map<String, String> pathVariables, Map<String, String> sharedPathVariables, Map<String, String> queryParams, Map<String, String> sharedQueryParams) {
        // Replace path variables in the URL
        for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
            baseUrl = baseUrl.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Replace shared path variables in the URL
        for (Map.Entry<String, String> entry : sharedPathVariables.entrySet()) {
            baseUrl = baseUrl.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Append query parameters if they are provided
        StringBuilder queryBuilder = new StringBuilder();
        
        // Add regular query parameters
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach((key, value) -> queryBuilder.append(key).append("=").append(value).append("&"));
        }
        
        // Add shared query parameters
        if (sharedQueryParams != null && !sharedQueryParams.isEmpty()) {
            sharedQueryParams.forEach((key, value) -> queryBuilder.append(key).append("=").append(value).append("&"));
        }

        // Remove the last '&' if queryBuilder is not empty
        if (queryBuilder.length() > 0) {
            queryBuilder.setLength(queryBuilder.length() - 1); // Remove the last '&'
            baseUrl += "?" + queryBuilder.toString(); // Append the query string to the base URL
        }

        return baseUrl;
    }
}