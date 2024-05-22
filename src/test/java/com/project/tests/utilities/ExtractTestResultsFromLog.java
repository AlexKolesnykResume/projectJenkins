package com.project.tests.utilities;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtractTestResultsFromLog {
    private static List<String> testResultFinal = null;
    private static List<String> failedScenariosList = null;
    private static List<String> logs = null;
    private static final String source = ConfigProvider.getAsString("executionLog");

    @Test
    public void test() {
//        String browser = getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.Browser.name());
//        System.out.println(browser);
//        String environment = getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.Environment.name());
//        System.out.println(environment);
//        String applicationUrl = getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.GOOGLEURL.name());
//        System.out.println(applicationUrl);

//        String deploymentVersion = getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.DeploymentVersion.name());
//        System.out.println(deploymentVersion);

//        String deploymentVersion = getDeployedVersion();
//        System.out.println(deploymentVersion);

        String deploymentVersion = getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.BuildStatus.name());
        System.out.println(deploymentVersion);


//        ExtractTestResultsFromLog.get("Pass");
    }

    public static void main(String[] args) {
        String logString = "[WARN] 20:23:12 SystemVsConfigProp.getProperty[26] - Value for property [Browser] is [ChromeOnLocal]";
        String browserValue = extractBrowserValue(logString, "Browser");
        System.out.println(browserValue);

        String envStringString = "[WARN] 20:23:12 SystemVsConfigProp.getProperty[26] - Value for property [Environment] is [CloudStage]";
        String environmentValue = extractBrowserValue(envStringString, "Environment");
        System.out.println(environmentValue);
    }

    public static String extractBrowserValue(String logString, String propertyName) {
        String regex = "\\[" + propertyName + "] is \\[([^]]+)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(logString);
        String browserValue = "";

        if (matcher.find()) {
            browserValue =  matcher.group(1);
        }
        return browserValue;
    }

    static {
        try (Stream<String> lines = Files.lines(Paths.get(System.getProperty("user.dir") + File.separator + source))) {
            logs = lines.collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Failing to read logFromProperties.txt. Exception: " + e.getMessage());
        }
    }

    static String getResult(String string) {
        String result = "";
        List<String> testsResult = getTestResult();
        if (!(testsResult == null || testsResult.isEmpty())) {
            String[] list = testsResult.get(0).split(",");
            for (String str : list) {
                result = str.replace("[INFO]", "");
                break;
            }
            if (result.isEmpty()) {
                result = "Failed to get " + string + " from " + source;
            }
        } else {
            result = "Failed to get " + string + " from " + source;
        }
        return result;
    }

    public static String getMavenOptions(String key) {
        String mavenOption = null;
        if (logs != null) {
            List<String> testsResultsAll = logs.stream().filter(item -> item.contains(key)).toList();
            if (!testsResultsAll.isEmpty()) {
                mavenOption = testsResultsAll.get(0);
                String regex = "\\[" + key + "] is \\[([^]]+)]";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(testsResultsAll.get(0));
                if (matcher.find()) {
                    mavenOption =  matcher.group(1);
                }
            } else {
                System.out.println("Filed to get Maven Options from log file for key [" + key + "]");
            }
        }
        return mavenOption;
    }

    private static List<String> getTestResult() {
        if (logs != null) {
            List<String> testsResultsAll = logs.stream().filter(item -> item.contains("Tests run:")).toList();
            if (!testsResultsAll.isEmpty()) {
                testResultFinal = testsResultsAll.stream().filter(item -> !item.contains("Parallel")).collect(Collectors.toList());
            } else {
                System.out.println("Failed to get tests Results from [" + source + "]");
            }
        }
        return testResultFinal;
    }

    private static List<String> getFailedScenarios() {
        if (logs != null) {
            List<String> failedScenariosTemp = logs.stream().filter(item -> item.contains("Status  --  Fail")).toList();
            if (!failedScenariosTemp.isEmpty()) {
                failedScenariosList = failedScenariosTemp.stream().distinct().collect(Collectors.toList());
            } else {
                System.out.println("Failed scenarios [0]");
            }
        }
        return failedScenariosList;
    }

    public static String get(String status) {
        String returnStatus = null;
        switch (status) {
            case "Pass":
                returnStatus = getPassedTestCount();
            case "Fail":
                returnStatus = getCount("Failures:");
            case "Skipped":
                returnStatus = getCount("Skipped:");
            case "Total Count":
                returnStatus = getCount("Tests run:");
            case "Build Status":
                returnStatus = getStatus();
            default:
                System.out.println("No supported status in get(" + status + ")");
        }
        return returnStatus;
    }

    private static String getStatus() {
        String status;
        try {
            int fail = Integer.parseInt(getCount("Failures:"));
            if (fail > 0) {
                status = "Still failing";
            } else {
                status = "Successful";
            }
        } catch (Exception e) {
            status = "Still failing";
        }
        return status;
    }

    private static String getCount(String string) {
        String testCount;
        try {
            String[] str = getResult(string).split(":");
            testCount = str[1].trim();
        } catch (Exception e) {
            testCount = "Failed to get the test count.";
        }
        return testCount;
    }

    private static String getPassedTestCount() {
        String pass;
        try {
            int total = Integer.parseInt(getCount("Tests run:"));
            int fail = Integer.parseInt(getCount("Failures:"));
            int skipped = Integer.parseInt(getCount("Skipped:"));
            pass = String.valueOf(total - fail - skipped);
        } catch (Exception e) {
            pass = "Failed to get the tests count.";
        }
        return pass;
    }

//    public static String getDeployedVersion() {
//        String deployedVersion = null;
//        if (logs != null) {
//            List<String> testsResultsAll = logs.stream().filter(item -> item.contains("==>Deployed Version")).toList();
//            if (!testsResultsAll.isEmpty()) {
//                deployedVersion = testsResultsAll.get(0).replace("==>Deployed", "")
//                        .replace("Version", "")
//                        .replace(":", "").trim();
//            } else {
//                System.out.println("Failed to get the deployed version from the log file.");
//            }
//        }
//        return deployedVersion;
//    }
//
//    public static String getApplicationUrl() {
//        String url = null;
//        if (logs != null) {
//            List<String> testsResultsAll = logs.stream().filter(item -> item.contains("Applicationurl==>")).toList();
//            if (!testsResultsAll.isEmpty()) {
//                url = testsResultsAll.get(0).replace("Applicationurl==>", "").trim();
//            } else {
//                System.out.println("Failed to get the Application Url  from the log file.");
//            }
//        }
//        return url;
//    }
}
