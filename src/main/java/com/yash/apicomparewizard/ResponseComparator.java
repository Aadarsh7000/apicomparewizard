package com.yash.apicomparewizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;



public class ResponseComparator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<String> compareResponses(String oldResponse, String newResponse, List<String> fieldsToCompare, String uniqueField) throws Exception {
        List<String> mismatches = new ArrayList<>();
        JsonNode oldNode = objectMapper.readTree(oldResponse);
        JsonNode newNode = objectMapper.readTree(newResponse);
        
        try {
        assertJsonEquals(oldResponse, newResponse);
        }catch (AssertionError e) {
        	 System.err.println("Differences found:\n" + e.getMessage());
        	 mismatches.add(e.getMessage());

		}
//       
//        if (fieldsToCompare.isEmpty()) {
//            compareJsonNodes(oldNode, newNode, mismatches, "", uniqueField);
//        } else {
//            for (String field : fieldsToCompare) {
//                JsonNode oldField = oldNode.path(field);
//                JsonNode newField = newNode.path(field);
//                if (!oldField.equals(newField)) {
//                    mismatches.add("Mismatch in field: " + field + " (Old: " + oldField + ", New: " + newField + ")");
//                }
//            }
//        }
        return mismatches;
    }

//    private static void compareJsonNodes(JsonNode oldNode, JsonNode newNode, List<String> mismatches, String path, String uniqueField) {
//        if (oldNode.isObject() && newNode.isObject()) {
//            // Compare fields in objects
//            Iterator<String> fieldNames = oldNode.fieldNames();
//            while (fieldNames.hasNext()) {
//                String fieldName = fieldNames.next();
//                JsonNode oldField = oldNode.get(fieldName);
//                JsonNode newField = newNode.get(fieldName);
//                String currentPath = path.isEmpty() ? fieldName : path + "." + fieldName;
//
//                if (newField == null) {
//                    mismatches.add("Field missing in new response: " + currentPath);
//                } else {
//                    compareJsonNodes(oldField, newField, mismatches, currentPath, uniqueField);
//                }
//            }
//
//          Iterator<String> newFieldNames = newNode.fieldNames();
//            while (newFieldNames.hasNext()) {
//                String fieldName = newFieldNames.next();
//                if (!oldNode.has(fieldName)) {
//                    mismatches.add("Field missing in old response: " + path + "." + fieldName);
//                }
//            }
//        } else if (oldNode.isArray() && newNode.isArray()) {
//           Map<String, JsonNode> newMap = new HashMap<>();
//            for (JsonNode newItem : newNode) {
//                String id = newItem.path(uniqueField).asText(); 
//                newMap.put(id, newItem);
//            }
//
//             for (int i = 0; i < oldNode.size(); i++) {
//                JsonNode oldItem = oldNode.get(i);
//                String id = oldItem.path(uniqueField).asText(); 
//                JsonNode newItem = newMap.get(id);
//                if (newItem == null) {
//                    mismatches.add("Missing item in new response with " + uniqueField + ": " + id);
//                } else {
//                    compareJsonNodes(oldItem, newItem, mismatches, path + "[" + i + "]", uniqueField);
//                }
//            }
//        } else if (!oldNode.equals(newNode)) {
//            mismatches.add("Mismatch at path: " + path + " (Old: " + oldNode + ", New: " + newNode + ")");
//        }
//    }
}

//package com.yash.apicomparewizard;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class ResponseComparator {
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    public static List<String> compareResponses(String oldResponse, String newResponse, List<String> fieldsToCompare) throws Exception {
//        List<String> mismatches = new ArrayList<>();
//        JsonNode oldNode = objectMapper.readTree(oldResponse);
//        System.out.print(oldNode.isArray());
//        JsonNode newNode = objectMapper.readTree(newResponse);
//        
//        if (fieldsToCompare.isEmpty()) {
//          compareJsonNodes(oldNode, newNode, mismatches, "");
//        } else {
//          for (String field : fieldsToCompare) {
//                JsonNode oldField = oldNode.path(field);
//                JsonNode newField = newNode.path(field);
//                if (!oldField.equals(newField)) {
//                    mismatches.add("Mismatch in field: " + field + " (Old: " + oldField + ", New: " + newField + ")");
//                }
//            }
//        }
//        return mismatches;
//    }
//
//   
//	private static void compareJsonNodes(JsonNode oldNode, JsonNode newNode, List<String> mismatches, String path) {
//         
//		 if (oldNode.isObject() && newNode.isObject()) {
//            Iterator<String> fieldNames = oldNode.fieldNames();
//            while (fieldNames.hasNext()) {
//                String fieldName = fieldNames.next();
//                JsonNode oldField = oldNode.get(fieldName);
//                JsonNode newField = newNode.get(fieldName);
//                String currentPath = path.isEmpty() ? fieldName : path + "." + fieldName;
//
//                if (newField == null) {
//                    mismatches.add("Field missing in new response: " + currentPath);
//                } else if (!oldField.equals(newField)) {
//                    mismatches.add("Mismatch in field: " + currentPath + " (Old: " + oldField + ", New: " + newField + ")");
//                }
//            }
//
//            Iterator<String> newFieldNames = newNode.fieldNames();
//            while (newFieldNames.hasNext()) {
//                String fieldName = newFieldNames.next();
//                if (!oldNode.has(fieldName)) {
//                    mismatches.add("Field missing in old response: " + path + "." + fieldName);
//                }
//            }
//        } else if (!oldNode.equals(newNode)) {
//            mismatches.add("Mismatch at path: " + path + " (Old: " + oldNode + ", New: " + newNode + ")");
//        }
//    }
//	
//	
//}