package com.yash.apicomparewizard;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
public class ConfigLoader {
   private Properties properties;
   public ConfigLoader(String propertiesFilePath) throws IOException {
       properties = new Properties();
       try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
           properties.load(fis);
       }
   }
   public String getProperty(String key) {
       return properties.getProperty(key);
   }
   public String[] getArrayProperty(String key, String delimiter) {
       String value = properties.getProperty(key);
       return value != null ? value.split(delimiter) : new String[0];
   }
}