package com.yash.apicomparewizard;

import org.springframework.http.HttpHeaders;

import java.util.Map;

public class HeaderBuilder {
    public static HttpHeaders buildHeader(Map<String, String> header) {
        HttpHeaders httpHeaders = new HttpHeaders();

        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }

        return httpHeaders;
    }
}