package com.project.tests.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import java.util.Properties;

public class SystemVsConfigProp {
    private static final Logger LOGGER = LogManager.getLogger(SystemVsConfigProp.class);

    public SystemVsConfigProp() {
        loadConfigProperties();
        setDefaultRunTimeProperties();
    }

    Properties runtimeProperties = null;

    public String getProperty(String propertyKey) {
        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue == null) {
            loadConfigProperties();
            setRunTimeProperties(propertyKey);
            propertyValue = System.getProperty(propertyKey);
        }
        Log.warning(LOGGER, "Value for property [{}] is [{}]", propertyKey,  propertyValue);
        return propertyValue;
//        String propValue = null;
//        try {
//            if (System.getProperty("runFrom").equals("pom")) {
//                propValue = System.getProperty(urlName);
//            } else {
//                propValue = configPropertiesFile.getProperty(urlName);
//            }
//        } catch (Exception e) {
//            Log.error(LOGGER, "Fail to read the property [" + urlName + "] from properties file [" + configFiles.configPropertiesFile + "].");
//            Assertions.fail("Fail to read the property [" + urlName + "] from properties file [" + configFiles.configPropertiesFile + "].");
//        }
//        return propValue;
    }

    public enum SystemPropertyVariables {
        Environment, Browser, tagToRun, Database, GOOGLEURL, SAUCE_USERNAME, SAUCE_KEY,
        TagName, DeploymentVersion, TotalTests, PassedTests, SkippedTests, FailedTests, BuildStatus
    }

    private void loadConfigProperties() {
        runtimeProperties = ConfigProvider.getInstance();
    }

    private void setDefaultRunTimeProperties() {
        if (System.getProperty(SystemPropertyVariables.Environment.name()) == null) {
            System.setProperty(SystemPropertyVariables.Environment.name(), ConfigProvider.getAsString(SystemPropertyVariables.Environment.name()));
        }
        if (System.getProperty(SystemPropertyVariables.tagToRun.name()) == null) {
            System.setProperty(SystemPropertyVariables.tagToRun.name(), ConfigProvider.getAsString(SystemPropertyVariables.tagToRun.name()));
        }
        if (System.getProperty(SystemPropertyVariables.Browser.name()) == null) {
            System.setProperty(SystemPropertyVariables.Browser.name(), ConfigProvider.getAsString(SystemPropertyVariables.Browser.name()));
        }
        System.setProperty(SystemPropertyVariables.Database.name(), System.getProperty(SystemPropertyVariables.Environment.name()));
    }

    private void setRunTimeProperties(String urlName) {
        String environment = System.getProperty(SystemPropertyVariables.Environment.name()).toUpperCase();
        switch (environment) {
            case "STAGE" -> urlHelper(urlName, "Stage");
            case "STAGE2" -> urlHelper(urlName, "Stage2");
            case "TEST" -> urlHelper(urlName, "Test");
            case "TEST2" -> urlHelper(urlName,"Test2");
            case "CLOUDSTAGE" -> urlHelper(urlName,"CloudStage");
            default -> {
                Log.error(LOGGER, "[" + environment + "] environment does not exist.");
                Assertions.fail("[" + environment + "] environment does not exist.");
            }
        }
    }

    private void urlHelper(String urlName, String environment) {
        String urlNameAndEnvironment = urlName + "_" + environment;
        String fullUrlBasedOnUrlNameAndEnvironment = runtimeProperties.getProperty(urlNameAndEnvironment);
        if (fullUrlBasedOnUrlNameAndEnvironment == null || fullUrlBasedOnUrlNameAndEnvironment.isEmpty()) {
            Log.error(LOGGER, "[" + urlNameAndEnvironment + "] returned null. Add the URL for this environment in Config.properties file.");
            System.setProperty(urlName, "returned null. Add the URL for this environment in Config.properties file.");
            Assertions.fail("[" + urlNameAndEnvironment + "] returned null. Add the URL for this environment in Config.properties file.");
        } else {
            System.setProperty(urlName, fullUrlBasedOnUrlNameAndEnvironment);
        }
    }
}
