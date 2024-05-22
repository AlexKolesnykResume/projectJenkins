package com.project.utility.extentreports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class ExtentConfiguration {
    private static final Logger LOGGER = Logger.getLogger(ExtentConfiguration.class.getName());

    private static ExtentReports extent;
    public static final String WORKING_DIR = System.getProperty("user.dir");
    private static final String EXTENT_REPORT_FOLDER = WORKING_DIR + File.separator + "AutomationReports";
    private static final String TIME_STAMP = new SimpleDateFormat("dd.MM.yyyy.HH.mm").format(new Date());
    private static final String REPORT_NAME = "ExtentReport_" + TIME_STAMP + "_" + Thread.currentThread().threadId() + ".html";
    private static final String EXTENT_REPORTS_PATH = EXTENT_REPORT_FOLDER + File.separator + REPORT_NAME;

    private ExtentConfiguration() {
    }

//    public static String getExtentReportFolder() {
//        return EXTENT_REPORTS_PATH;
//    }

    public static ExtentReports getInstance() {
        if (extent == null) {
            createReportFolder();
            attachReporters();
        }
        return extent;
    }

    private static void createReportFolder() {
        File file = new File(EXTENT_REPORT_FOLDER);
        if (!file.exists() && !file.mkdir()) {
            LOGGER.warning("Failed to create directory!");
        } else {
            LOGGER.info("Directory [" + file + "] was created successfully.");
        }
    }

    public static ExtentSparkReporter initHtmlReporter() {
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(EXTENT_REPORTS_PATH);
        htmlReporter.config().setTheme(Theme.DARK);
        htmlReporter.config().setDocumentTitle(REPORT_NAME);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName("Execution-Status");
//        htmlReporter.config().setCss("css-string");
//        htmlReporter.config().setJs("js-string");
        htmlReporter.config().setProtocol(Protocol.HTTPS);
        htmlReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        htmlReporter.config().setTimelineEnabled(true);
        return htmlReporter;
    }

    private static void attachReporters() {
        String extentReportRequired = null;
        extent = new ExtentReports();
        extent.attachReporter(initHtmlReporter());
    }
}
