package com.project.pages;

import com.project.tests.utilities.SeleniumUtils;
import com.project.tests.utilities.SystemVsConfigProp;
import org.junit.jupiter.api.Assertions;

import java.sql.Connection;
import java.util.Base64;
import java.util.Properties;

public class CommonSteps extends SeleniumUtils {
    public static Connection dbConnection = null;
    protected static SystemVsConfigProp systemVsConfigProp = new SystemVsConfigProp();
    public static Properties runtimeProperties = new Properties();
    public static void putRunTimeProperty(String key, String value) {
        runtimeProperties.put(key, value);
    }

    public static void waitForSeconds(double seconds) {
        try {
            long milliseconds = (long) (seconds * 1000);
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assertions.fail("Failed to wait for seconds. Exception: " + e.getMessage());
        }
    }

    public static String getRunTimeProperty(String key) {
        String value = "";
        try {
            Object propertyValue = runtimeProperties.get(key);
            if (propertyValue != null) {
                value = propertyValue.toString();
                return replaceArgumentsWithRunTimeProperties(value);
            } else {
                throw new Exception("Property not found for key: [" + key +"].");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail(e.getMessage());
        }
        return value;
    }

    public static String replaceArgumentsWithRunTimeProperties(String input) {
        String str = "select * from ole.table1 where name = {$name} and lastName = {$lastName}";
        if (input.contains("{$")) {
            int firstIndex = input.indexOf("{$");
            int lastIndex = input.indexOf("}", firstIndex);
            String key = input.substring(firstIndex + 2, lastIndex);
            String value = getRunTimeProperty(key);
            input = input.replace("{$" + key + "}", value);
            System.out.println("Key [" + key + "] replaced with value [" + value + "].");
            return replaceArgumentsWithRunTimeProperties(input);
        } else {
            return input;
        }
    }

    public static String decodeBase64(String encodedText) {
        return new String(Base64.getDecoder().decode(encodedText.getBytes()));
    }
}
