package com.yash.apicomparewizard;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.http.HttpHeaders;

public class ApiRequestHandler {

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static String sendRequest(String url, String method, String requestBody, HttpHeaders headers)
            throws IOException, InterruptedException {

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url));

        for (String headerName : headers.keySet()) {
            for (String headerValue : headers.get(headerName)) {
                requestBuilder.header(headerName, headerValue);
            }
        }

        switch (method.toUpperCase()) {
            case "POST":
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
                break;

            case "PUT":
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(requestBody));
                break;

            case "GET":
            default:
                requestBuilder.GET();
                break;
        }

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
