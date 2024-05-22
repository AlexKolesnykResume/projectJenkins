package com.project.tests.utilities;

import com.project.utility.extentreports.Screenshots;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

public class Log {
    /**
     * Logs an informational message.
     * Logs a pass message.
     * Logs a warning message.
     * Logs an error message.
     *
     * @param logger  The logger instance.
     * @param message The message template with placeholders.
     * @param args    The arguments to replace placeholders.
     * @throws IllegalArgumentException If fewer arguments are provided than placeholders.
     */
    public static void info(Logger logger, Object message, Object @NotNull ... args) {
        int expectedArgs = countPlaceholders(String.valueOf(message));
        if (args.length != expectedArgs) {
            throw new IllegalArgumentException("Mismatch between the number of arguments (" + args.length + ") and " +
                    "the number of placeholders specified (" + expectedArgs + "). Message [" + message + "].");
        }
        if (logger.isInfoEnabled()) {
            logger.info(readStackTrace(message), args);
        }
    }

    public static void warning(Logger logger, Object message, Object @NotNull ... args) {
        int expectedArgs = countPlaceholders(String.valueOf(message));
        if (args.length != expectedArgs) {
            throw new IllegalArgumentException("Mismatch between the number of arguments (" + args.length + ") and " +
                    "the number of placeholders specified (" + expectedArgs + "). Message [" + message + "].");
        }
        if (logger.isWarnEnabled()) {
            logger.warn(readStackTrace(message), args);
        }
    }

    public static void error(Logger logger, Object message, Object @NotNull ... args) {
        int expectedArgs = countPlaceholders(String.valueOf(message));
        if (args.length != expectedArgs) {
            throw new IllegalArgumentException("Mismatch between the number of arguments (" + args.length + ") and " +
                    "the number of placeholders specified (" + expectedArgs + "). Message [" + message + "].");
        }
        if (logger.isErrorEnabled()) {
            logger.error(readStackTrace(message), args);
        }
    }

    // IMPORTANT: use this method when you need a specific step to take a screenshot for the extent report
    public static void addInfoStepToExtentReport(Logger logger, Object message, Object @NotNull ... args) {
        int expectedArgs = countPlaceholders(String.valueOf(message));
        if (args.length != expectedArgs) {
            throw new IllegalArgumentException("Mismatch between the number of arguments (" + args.length + ") and " +
                    "the number of placeholders specified (" + expectedArgs + "). Message [" + message + "].");
        }
        if (logger.isInfoEnabled()) {
            logger.info(readStackTrace(message), args);
            if (ConfigProvider.getAsString("isExtentReportRequired").equals("true")) {
                Screenshots.addInfoStepWithScreenshotInReport(Driver.getDriver(), readStackTrace(message));
            }
        }
    }

    public static void addPassStepToExtentReport(Logger logger, Object message, Object @NotNull ... args) {
        int expectedArgs = countPlaceholders(String.valueOf(message));
        if (args.length != expectedArgs) {
            throw new IllegalArgumentException("Mismatch between the number of arguments (" + args.length + ") and " +
                    "the number of placeholders specified (" + expectedArgs + "). Message [" + message + "].");
        }
        if (logger.isInfoEnabled()) {
            logger.info(readStackTrace(message), args);
            if (ConfigProvider.getAsString("isExtentReportRequired").equals("true")) {
                Screenshots.addPassStepWithScreenshotInReport(Driver.getDriver(), readStackTrace(message));
            }
        }
    }

    public static void addWarningStepToExtentReport(Logger logger, Object message, Object @NotNull ... args) {
        int expectedArgs = countPlaceholders(String.valueOf(message));
        if (args.length != expectedArgs) {
            throw new IllegalArgumentException("Mismatch between the number of arguments (" + args.length + ") and " +
                    "the number of placeholders specified (" + expectedArgs + "). Message [" + message + "].");
        }
        if (logger.isWarnEnabled()) {
            logger.warn(readStackTrace(message), args);
            if (ConfigProvider.getAsString("isExtentReportRequired").equals("true")) {
                Screenshots.addWarningStepWithScreenshotInReport(Driver.getDriver(), readStackTrace(message));
            }
        }
    }

    public static void addErrorStepToExtentReport(Logger logger, Object message, Object @NotNull ... args) {
        int expectedArgs = countPlaceholders(String.valueOf(message));
        if (args.length != expectedArgs) {
            throw new IllegalArgumentException("Mismatch between the number of arguments (" + args.length + ") and " +
                    "the number of placeholders specified (" + expectedArgs + "). Message [" + message + "].");
        }
        if (logger.isErrorEnabled()) {
            logger.error(readStackTrace(message), args);
            if (ConfigProvider.getAsString("isExtentReportRequired").equals("true")) {
                Screenshots.addFailureStepWithScreenshotInReport(Driver.getDriver(), readStackTrace(message));
            }
        }
    }

    private static String readStackTrace(Object message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//        for (StackTraceElement trace : stackTrace) {
//            System.out.println("stack trace: " + trace);
//        }

        int index = -1;
        switch (stackTrace.length) {
            case 4, 5, 29, 52, 53, 54, 57, 72 ,    36, 37, 39, 40, 41, 42, 43, 65, 66, 67, 68, 69 -> index = 3;
            case 73 -> index = 4;
            case 55, 56 -> index = 5;
            default -> Assertions.fail("StackTrace index [" + index + "] does not exist. The length is [" + stackTrace.length + "].");
        }

        StackTraceElement caller = stackTrace[index];
        String className = caller.getClassName();
        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex != -1) {
            className = className.substring(lastDotIndex + 1);
        }
//        System.out.println("stackTrace size: " + stackTrace.length);
//        System.out.println("className1: " + className);
        if (className.contains("StepDef")) {
            index = 3;
            caller = stackTrace[index];
            className = caller.getClassName();
            lastDotIndex = className.lastIndexOf('.');
            if (lastDotIndex != -1) {
                className = className.substring(lastDotIndex + 1);
            }
            if (className.contains("SeleniumUtils")) {
                index = 4;
                caller = stackTrace[index];
                className = caller.getClassName();
                lastDotIndex = className.lastIndexOf('.');
                if (lastDotIndex != -1) {
                    className = className.substring(lastDotIndex + 1);
                }
            }
        }
//        System.out.println("className2: " + className);
        return String.format("%s.%s[%d] - %s", className, caller.getMethodName(), caller.getLineNumber(), message);
//        return String.format("%s[%d] - %s", caller.getMethodName(), caller.getLineNumber(), message);

    }

    private static int countPlaceholders(@NotNull String message) {
        int count = 0;
        int index = message.indexOf("{}");
        while (index != -1) {
            count++;
            index = message.indexOf("{}", index + 2);
        }
        return count;
    }
}
