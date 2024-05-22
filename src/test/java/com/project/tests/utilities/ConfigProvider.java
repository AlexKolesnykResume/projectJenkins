package com.project.tests.utilities;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ConfigProvider {
    private static Properties props;
    private static final Map<String, Properties> configMap = new HashMap<>();
    private static final String PROPERTIES_EXT = ".properties";
    private static final String PROPERTIES_FOLDER = System.getProperty("user.dir") + File.separator + "src" +
            File.separator + "test" + File.separator + "resources" + File.separator + "ApplicationFiles" +
            File.separator + "ConfigFiles" + File.separator;

    private ConfigProvider() {
    }

    private static Properties getInstance(String propertyFileName) {
        Properties props;
        if (configMap.isEmpty()) {
            props = loadProperties(propertyFileName);
            configMap.put(propertyFileName, props);
            return props;
        }
        for (Map.Entry<String, Properties> entry : configMap.entrySet()) {
            if (entry.getKey().equals(propertyFileName)) {
                return entry.getValue();
            }
        }
        props = loadProperties(propertyFileName);
        configMap.put(propertyFileName, props);
        return props;
    }

    public static Properties getInstance() {
        if (props == null) {
            props = loadProperties();
        }
        return props;
    }

    private static @NotNull Properties loadProperties() {
        Properties props = new Properties();
        File propertiesFile;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
           propertiesFile = new File(PROPERTIES_FOLDER);
        } catch (NullPointerException e) {
            System.err.println("No properties file found inside " + PROPERTIES_FOLDER + " folder. Please add all properties files under mentioned folder (create folder if doesn't exist).");
            throw new NullPointerException();
        }
        try {
            File[] listOfFiles = propertiesFile.listFiles();
            if (listOfFiles == null) {
                Assertions.fail("List of files is null.");
            }
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(PROPERTIES_EXT)) {
                    props.load(new FileInputStream(propertiesFile + File.separator + file.getName()));
                }
            }
        } catch (IOException e) {
            System.err.println("Not able to load the property!!!");
        }
        return props;
    }

    private static @NotNull Properties loadProperties(String propertyFileName) {
        Properties props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream(PROPERTIES_FOLDER + File.separator + propertyFileName + PROPERTIES_EXT);
        try {
            props.load(is);
        } catch (NullPointerException e) {
            System.err.println("'" + propertyFileName + PROPERTIES_EXT + "' file was not found. " + PROPERTIES_FOLDER + " folder. Please verify mentioned file should be present under 'properties' folder(create folder if it doesn't exist).");
            throw new NullPointerException();
        } catch (IOException e) {
            System.err.println("Not able to load the property!!!");
        }
        return props;
    }

    public static String getAsString(String key) {
        return getInstance().getProperty(key);
    }


    public static boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(getInstance().getProperty(key));
    }
}
