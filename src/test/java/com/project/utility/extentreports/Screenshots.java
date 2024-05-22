package com.project.utility.extentreports;

import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class Screenshots {
    private static final String SCREENSHOTS_FOLDER =  File.separator + "AutomationReports" + File.separator + "screenshots" + File.separator;
    private static final Logger LOGGER = Logger.getLogger(ExtentConfiguration.class.getName());
    private static String SCREENSHOTS_FOLDER_PATH;

    static {
        createDirectory();
    }

    public static void addInfoStepWithScreenshotInReport(WebDriver driver, String message) {
        if (driver != null) {
            String path = captureScreenshot(driver);
            ExtentTestManager.getTest().info(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
        } else {
            ExtentTestManager.getTest().info(message);
        }
    }

    public static void addPassStepWithScreenshotInReport(WebDriver driver, String message) {
        if (driver != null) {
            String path = captureScreenshot(driver);
            ExtentTestManager.getTest().pass(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
        } else {
            ExtentTestManager.getTest().pass(message);
        }
    }

    public static void addWarningStepWithScreenshotInReport(WebDriver driver, String message) {
        if (driver != null) {
            String path = captureScreenshot(driver);
            ExtentTestManager.getTest().warning(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
        } else {
            ExtentTestManager.getTest().pass(message);
        }
    }

    public static void addFailureStepWithScreenshotInReport(WebDriver driver, String message) {
        if (driver != null) {
            String path = captureScreenshot(driver);
            ExtentTestManager.getTest().fail(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
        } else {
            ExtentTestManager.getTest().pass(message);
        }
    }

    private static void createDirectory() {
        SCREENSHOTS_FOLDER_PATH = System.getProperty("user.dir") + SCREENSHOTS_FOLDER;
        if (!new File(SCREENSHOTS_FOLDER_PATH).exists()) {
            File file = new File(SCREENSHOTS_FOLDER_PATH);
            if (!file.exists() && file.mkdir()) {
                LOGGER.warning("Failed to create directory!");
            } else {
                LOGGER.info("screenshot directory: " + SCREENSHOTS_FOLDER);
            }
        }
    }

    protected static String captureScreenshot(WebDriver driver) {
        String randomNumber = RandomStringUtils.randomNumeric(5);
        String destinationPath = SCREENSHOTS_FOLDER_PATH + "screenshot" + randomNumber + ".jpg";
        String impPath = ".." + SCREENSHOTS_FOLDER + "screenshot" + randomNumber + ".jpg";
        TakesScreenshot ts = (TakesScreenshot) driver;
        File srcFile = ts.getScreenshotAs(OutputType.FILE);
        try {
//            updateTimeStamp(srcFile, destinationPath);
            FileUtils.copyFile(srcFile, new File(destinationPath));
        } catch (IOException e) {
            LOGGER.warning("Not able to capture screenshot!");
        }
        return impPath;
    }

    public static void updateTimeStamp(File sourceFileName, String targetFileName) {
        final BufferedImage image;
        try {
            image = ImageIO.read(sourceFileName);
            SimpleDateFormat formatter = new SimpleDateFormat();
            Graphics graphics = image.getGraphics();
            graphics.setFont(graphics.getFont().deriveFont(18f));
            graphics.setColor(new Color(255, 20, 20));
            Date date = new Date(System.currentTimeMillis());
            graphics.drawString(formatter.format(date), image.getWidth() - 250, image.getHeight() - 20);
            graphics.dispose();
            ImageIO.write(image, "png", new File(targetFileName));
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
    }
}
