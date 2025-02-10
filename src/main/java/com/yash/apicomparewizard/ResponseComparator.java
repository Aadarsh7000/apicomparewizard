package com.yash.apicomparewizard;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.core.listener.Difference;


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
            for (Object key : oldMap.keySet()) {
            	   CustomDifferenceListener listener = new CustomDifferenceListener();
                   
                   Configuration config = Configuration.empty()
                           .withOptions(Option.IGNORING_ARRAY_ORDER).withDifferenceListener(listener);
                
                if (newMap.containsKey(key)) {
                    try {
                        assertJsonEquals(objectMapper.writeValueAsString(oldMap.get(key)),
                                objectMapper.writeValueAsString(newMap.get(key)),
                                config);
                    } catch (AssertionError e) {	
                    	List<Difference> diff=listener.getDifferences();
       				for (Difference difference : diff) {
							  Map<String, String> mismatch = new HashMap<>();
							if(difference.getType().toString()== "DIFFERENT") {
								  String type=difference.getType().toString();
								  String fieldName = difference.getExpectedPath();
                                  String expectedValues = difference.getExpected().toString();
                                  String actualValues = difference.getActual().toString();
                                  String combined = type+","+ fieldName + "," + expectedValues.replace(",", "*") + ","
										+ actualValues.replace(",", "*");
								mismatch.put(key.toString(), combined);
							} else if (difference.getType().toString() == "MISSING") {
								String type = difference.getType().toString();
								String fieldName = difference.getExpectedPath();
								String expectedValues = difference.getExpected().toString();
								String combined = type + "," + fieldName + "," + expectedValues.replace(",", "*") + ","+"Not Present";
								mismatch.put(key.toString(), combined);
							} else if (difference.getType().toString() == "EXTRA") {
								String type = difference.getType().toString();
								String fieldName = difference.getActualPath();
								String actualValues = difference.getActual().toString();
								String combined = type + "," + fieldName + "," + "Not Present" + ","+ actualValues.replace(",", "*");
								mismatch.put(key.toString(), combined);
							}
							mismatches.add(mismatch);
						}
					}

				} else {
					Map<String, String> mismatch = new HashMap<>();
					mismatch.put(key.toString(), "Missing in new response: " + oldMap.get(key));
					mismatches.add(mismatch);

				}

			}

		} else {
			CustomDifferenceListener listener = new CustomDifferenceListener();
			Configuration config = Configuration.empty().withOptions(Option.IGNORING_ARRAY_ORDER)
					.withDifferenceListener(listener);
			try {
				assertJsonEquals(oldResponse, newResponse, config);
			} catch (AssertionError e) {
				List<Difference> diff=listener.getDifferences();
   				for (Difference difference : diff) {
						  Map<String, String> mismatch = new HashMap<>();
						if(difference.getType().toString()== "DIFFERENT") {
							String type=difference.getType().toString();
							String fieldName = difference.getExpectedPath();
                            String expectedValues = difference.getExpected().toString();
                            String actualValues = difference.getActual().toString();
                            String combined = type+","+ fieldName + "," + expectedValues.replace(",", "*") + ","
									+ actualValues.replace(",", "*");
							mismatch.put("Overall Mismatch", combined);
						} else if (difference.getType().toString() == "MISSING") {
							String type = difference.getType().toString();
							String fieldName = difference.getExpectedPath();
							String expectedValues = difference.getExpected().toString();
							String combined = type + "," + fieldName + "," + expectedValues.replace(",", "*") + ","+ "Not Present";
							mismatch.put("Overall Mismatch", combined);
						} else if (difference.getType().toString() == "EXTRA") {
							String type = difference.getType().toString();
							String fieldName = difference.getActualPath();
							String actualValues = difference.getActual().toString();
							String combined = type + "," + fieldName + "," + "Not Present" + ","+ actualValues.replace(",", "*");
							mismatch.put("Overall Mismatch", combined);
						}
						mismatches.add(mismatch);
					}
			}
		}

		return mismatches;
	}

	private static List<Map<String, Object>> parseJsonToList(String json) throws Exception {
		return objectMapper.readValue(json,
				objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
	}

	private static Map<Object, Map<String, Object>> createMapByUniqueField(List<Map<String, Object>> list,
			String uniqueField) {
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