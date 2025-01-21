package com.yash.apicomparewizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ResponseComparator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Map<String, String>> compareResponses(String oldResponse, String newResponse, String uniqueField) throws Exception {
        List<Map<String, String>> mismatches = new ArrayList<>();

        JsonNode oldJsonNode = objectMapper.readTree(oldResponse);
        JsonNode newJsonNode = objectMapper.readTree(newResponse);

        if (oldJsonNode.isArray() && newJsonNode.isArray()) {

            List<Map<String, Object>> oldList = parseJsonToList(oldResponse);
            List<Map<String, Object>> newList = parseJsonToList(newResponse);
            if (uniqueField.isEmpty() || uniqueField.isBlank() || uniqueField.equals("")) {
                uniqueField = "id";
            }
            Map<Object, Map<String, Object>> oldMap = createMapByUniqueField(oldList, uniqueField);
            Map<Object, Map<String, Object>> newMap = createMapByUniqueField(newList, uniqueField);

            Configuration config = Configuration.empty()
                    .withOptions(Option.IGNORING_ARRAY_ORDER);
            for (Object key : oldMap.keySet()) {
                if (newMap.containsKey(key)) {
                    try {
                        assertJsonEquals(objectMapper.writeValueAsString(oldMap.get(key)),
                                         objectMapper.writeValueAsString(newMap.get(key)),
                                         config);
                    } catch (AssertionError e) {
                        String errorMessage = e.getMessage();
                        if (errorMessage.startsWith("JSON documents are different:")) {
                            errorMessage = errorMessage.substring("JSON documents are different:".length()).trim();
                        }
                        String[] differences = errorMessage.split("\\n");
                        for (String difference : differences) {
                            if (!difference.trim().isEmpty()) { 
                                Map<String, String> mismatch = new HashMap<>();
                            
                                if (difference.contains("has different length") && difference.startsWith("Array") || difference.startsWith("Different value found when comparing expected array element")) {
                                    continue;
                                }
                                else if (difference.contains("Missing values") || difference.contains("extra values")) {
                                    String regex = "Array \"(.*?)\" has different content. Missing values: (.*), extra values: (.*), expected: <\\[(.*?)\\]> but was: <\\[(.*?)\\]>";
                                    Pattern pattern = Pattern.compile(regex);
                                    Matcher matcher = pattern.matcher(difference);
                                    if (matcher.find()) {
                                        String fieldName = matcher.group(1);
                                        String expectedValues = matcher.group(4);
                                        String actualValues = matcher.group(5);
                                        String combined = fieldName + "," + expectedValues.replace(",","*") + "," + actualValues.replace(",","*");
                                        mismatch.put(key.toString(), combined);
                                    }
								} else if (difference.contains("Different keys found in node")){
                                	 String regex = "Different keys found in node \".*?\", missing: \"(.*?)\", expected: <(.*?)> but was: <(.*?)>";
                                     Pattern pattern = Pattern.compile(regex);
                                     Matcher matcher = pattern.matcher(errorMessage);

                                     if (matcher.find()) {
                                         String fieldName = matcher.group(1);
                                         String expectedValue = matcher.group(2);
                                         String actualValue = matcher.group(3);
                              
                                        String combined = fieldName + "," + expectedValue.replace(",","*") + "," + actualValue.replace(",","*");
                                        mismatch.put(key.toString(), combined);
                                    }
                                	
                                } else {
                                    String regex = "Different value found in node \"(.*?)\", expected: <\"(.*?)\"> but was: <\"(.*?)\">";
                                    Pattern pattern = Pattern.compile(regex);
                                    Matcher matcher = pattern.matcher(difference);
                                    if (matcher.find()) {
                                        String fieldName = matcher.group(1);
                                        String oldValue = matcher.group(2);
                                        String newValue = matcher.group(3);
                                        String combined = fieldName + "," + oldValue.replace(",", "*") + "," + newValue.replace(",", "*");
                                        mismatch.put(key.toString(), combined);
                                    }
                                }
                                mismatches.add(mismatch);
                            }
                        }
                    }
                } else {
                    Map<String, String> mismatch = new HashMap<>();
                    mismatch.put( key.toString(), "Missing in new response: " + oldMap.get(key));
                    mismatches.add(mismatch);
                }
            }

        } else {
            Configuration config = Configuration.empty()
                    .withOptions(Option.IGNORING_ARRAY_ORDER);
            try {
                assertJsonEquals(oldResponse, newResponse, config);
            } catch (AssertionError e) {
                String errorMessage = e.getMessage();
                if (errorMessage.startsWith("JSON documents are different:")) {
                    // Remove the prefix
                    errorMessage = errorMessage.substring("JSON documents are different:".length()).trim();
                }
                String[] differences = errorMessage.split("\\n");
                for (String difference : differences) {
                    if (!difference.trim().isEmpty()) { 
                        Map<String, String> mismatch = new HashMap<>();
                        if (difference.contains("has different length") && difference.startsWith("Array") || difference.startsWith("Different value found when comparing expected array element")) {
                            continue;
                        }
                        // Check for missing or extra values
                        else if (difference.contains("Missing values") || difference.contains("extra values")) {
                            String regex = "Array \"(.*?)\" has different content. Missing values: (.*), extra values: (.*), expected: <\\[(.*?)\\]> but was: <\\[(.*?)\\]>";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(difference);
                            if (matcher.find()) {
                                String fieldName = matcher.group(1);
                                String expectedValues = matcher.group(4);
                                String actualValues = matcher.group(5);
                                String combined = fieldName + "," + expectedValues.replace(",","*") + "," + actualValues.replace(",","*");
                                mismatch.put("Overall mismatch", combined);
                            }
						} else if (difference.contains("Different keys found in node")){
                        	 String regex = "Different keys found in node \".*?\", missing: \"(.*?)\", expected: <(.*?)> but was: <(.*?)>";
                             Pattern pattern = Pattern.compile(regex);
                             Matcher matcher = pattern.matcher(errorMessage);

                             if (matcher.find()) {
                                 String fieldName = matcher.group(1);
                                 String expectedValue = matcher.group(2);
                                 String actualValue = matcher.group(3);
                      
                                String combined = fieldName + "," + expectedValue.replace(",","*") + "," + actualValue.replace(",","*");
                                mismatch.put("Overall mismatch", combined);
                            }
                        	
                        } else {
                            String regex = "Different value found in node \"(.*?)\", expected: <\"(.*?)\"> but was: <\"(.*?)\">";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(difference);
                            if (matcher.find()) {
                                String fieldName = matcher.group(1);
                                String oldValue = matcher.group(2);
                                String newValue = matcher.group(3);
                                String combined = fieldName + "," + oldValue.replace(",", "*") + "," + newValue.replace(",", "*");
                                mismatch.put("Overall mismatch", combined);
                            }
                        }
                        mismatches.add(mismatch);
                    }
                }
            }
        }

        return mismatches;
    }
    
    private static List<Map<String, Object>> parseJsonToList(String json) throws Exception {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    private static Map<Object, Map<String, Object>> createMapByUniqueField(List<Map<String, Object>> list, String uniqueField) {
        Map<Object, Map<String, Object>> map = new HashMap<>();
        for (Map<String, Object> item : list) {
            Object key = item.get(uniqueField);
            if (key != null) {
                map.put(key, item);
            }
        }
        return map;
    }
}
