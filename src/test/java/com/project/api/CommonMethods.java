package com.project.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

public class CommonMethods {
    private static final Logger LOGGER = LogManager.getLogger(CommonMethods.class);

    public static String getStatusCodeName(int statusCode) {
        String name;
        switch (statusCode) {
            case 0:
                name = "An Error Occurred";
                break;
            case 100:
                name = "Continue";
                break;
            case 200:
                name = "OK";
                break;
            default:
                name = "Status code name not specified in getStatusCodeName method, please add it.";
                Assertions.fail("Status code name not specified in getStatusCodeName method, please add it.");
        }
        return name;
    }
}
