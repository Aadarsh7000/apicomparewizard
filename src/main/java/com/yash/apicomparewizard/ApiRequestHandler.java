package com.yash.apicomparewizard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class ApiRequestHandler {
    public static String sendRequest(String url, String method, String requestBody) throws IOException, ParseException {

        ClassicHttpResponse response;
        switch (method.toUpperCase()) {
            case "POST":
                response = (ClassicHttpResponse) Request.post(url)
                        .bodyString(requestBody, org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
                        .execute()
                        .returnResponse();
                break;
            case "PUT":
                response = (ClassicHttpResponse) Request.put(url)
                        .bodyString(requestBody, org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
                        .execute()
                        .returnResponse();
                break;
            case "GET":
            default:
                response = (ClassicHttpResponse) Request.get(url).execute().returnResponse();
                break;
        }
        return EntityUtils.toString(response.getEntity());
    }
}
