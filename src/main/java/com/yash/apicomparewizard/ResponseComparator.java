//package com.yash.apicomparewizard;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.util.*;
//public class ResponseComparator {
//   private static final ObjectMapper objectMapper = new ObjectMapper();
//   public static List<String> compareResponses(String oldResponse, String newResponse, List<String> fieldsToCompare) throws Exception {
//       List<String> mismatches = new ArrayList<>();
//       JsonNode oldNode = objectMapper.readTree(oldResponse);
//       JsonNode newNode = objectMapper.readTree(newResponse);
//       if (fieldsToCompare.isEmpty()) {
//           compareJsonNodes(oldNode, newNode, mismatches, "");
//       } else {
//           for (String field : fieldsToCompare) {
//               JsonNode oldField = oldNode.path(field);
//               JsonNode newField = newNode.path(field);
//               compareJsonNodes(oldField, newField, mismatches, field);
//           }
//       }
//       return mismatches;
//   }
//   private static void compareJsonNodes(JsonNode oldNode, JsonNode newNode, List<String> mismatches, String path) {
//       if (oldNode == null && newNode == null) {
//           return;
//       } else if (oldNode == null || newNode == null) {
//           mismatches.add("Field missing at path: " + path);
//           return;
//       }
//       if (oldNode.isObject() && newNode.isObject()) {
//           Set<String> allFields = new HashSet<>();
//           oldNode.fieldNames().forEachRemaining(allFields::add);
//           newNode.fieldNames().forEachRemaining(allFields::add);
//           for (String field : allFields) {
//               compareJsonNodes(oldNode.get(field), newNode.get(field), mismatches, path + "." + field);
//           }
//       } else if (oldNode.isArray() && newNode.isArray()) {
//           compareJsonArrays(oldNode, newNode, mismatches, path);
//       } else if (!oldNode.equals(newNode)) {
//           mismatches.add("Mismatch at path: " + path + " | Old: " + oldNode + ", New: " + newNode);
//       }
//   }
//   private static void compareJsonArrays(JsonNode oldArray, JsonNode newArray, List<String> mismatches, String path) {
//       if (oldArray.size() != newArray.size()) {
//           mismatches.add("Array size mismatch at path: " + path + " | Old size: " + oldArray.size() + ", New size: " + newArray.size());
//       }
//       List<JsonNode> oldList = new ArrayList<>();
//       List<JsonNode> newList = new ArrayList<>();
//       oldArray.forEach(oldList::add);
//       newArray.forEach(newList::add);
//       for (JsonNode oldElement : oldList) {
//           if (!newList.contains(oldElement)) {
//               mismatches.add("Element missing in new array at path: " + path + " | Value: " + oldElement);
//           }
//       }
//       for (JsonNode newElement : newList) {
//           if (!oldList.contains(newElement)) {
//               mismatches.add("Extra element in new array at path: " + path + " | Value: " + newElement);
//           }
//       }
//   }
//}
package com.yash.apicomparewizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseComparator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<String> compareResponses(String oldResponse, String newResponse, List<String> fieldsToCompare) throws Exception {
        List<String> mismatches = new ArrayList<>();
        JsonNode oldNode = objectMapper.readTree(oldResponse);
        JsonNode newNode = objectMapper.readTree(newResponse);

        if (fieldsToCompare.isEmpty()) {
          compareJsonNodes(oldNode, newNode, mismatches, "");
        } else {
          for (String field : fieldsToCompare) {
                JsonNode oldField = oldNode.path(field);
                JsonNode newField = newNode.path(field);
                if (!oldField.equals(newField)) {
                    mismatches.add("Mismatch in field: " + field + " (Old: " + oldField + ", New: " + newField + ")");
                }
            }
        }
        return mismatches;
    }

    private static void compareJsonNodes(JsonNode oldNode, JsonNode newNode, List<String> mismatches, String path) {
        if (oldNode.isObject() && newNode.isObject()) {
            Iterator<String> fieldNames = oldNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode oldField = oldNode.get(fieldName);
                JsonNode newField = newNode.get(fieldName);
                String currentPath = path.isEmpty() ? fieldName : path + "." + fieldName;

                if (newField == null) {
                    mismatches.add("Field missing in new response: " + currentPath);
                } else if (!oldField.equals(newField)) {
                    mismatches.add("Mismatch in field: " + currentPath + " (Old: " + oldField + ", New: " + newField + ")");
                }
            }

            Iterator<String> newFieldNames = newNode.fieldNames();
            while (newFieldNames.hasNext()) {
                String fieldName = newFieldNames.next();
                if (!oldNode.has(fieldName)) {
                    mismatches.add("Field missing in old response: " + path + "." + fieldName);
                }
            }
        } else if (!oldNode.equals(newNode)) {
            mismatches.add("Mismatch at path: " + path + " (Old: " + oldNode + ", New: " + newNode + ")");
        }
    }
}