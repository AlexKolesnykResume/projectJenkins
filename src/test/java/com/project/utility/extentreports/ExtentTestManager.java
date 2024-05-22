package com.project.utility.extentreports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExtentTestManager {
    private ExtentTestManager() {
    }

    private static final Map<Integer, ExtentTest> extentTestMap = new HashMap<>();
    private static final Set<Integer> extentThreadList = new HashSet<>();
    private static ExtentReports extent;

    public static synchronized ExtentTest getTest() {
        return extentTestMap.get(getCurrentThread());
    }

    public static synchronized void endTest() {
        if (!extentTestMap.isEmpty()) {
            extent.removeTest(extentTestMap.get(getCurrentThread()));
        }
    }

    public static synchronized void initExtentConfiguration() {
        extent = ExtentConfiguration.getInstance();
    }

    public static synchronized void startTest(String testName, final String description) {
//        extent = ExtentConfiguration.getInstance();
        ExtentTest test = extent.createTest(testName, description);
        extentTestMap.put(getCurrentThread(), test);
        extentThreadList.add(getCurrentThread());
    }

    private static synchronized int getCurrentThread() {
        return (int) (Thread.currentThread().threadId());
    }
}
