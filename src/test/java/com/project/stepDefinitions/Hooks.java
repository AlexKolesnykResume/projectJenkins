package com.project.stepDefinitions;

import com.project.PlaygroundTest;
import com.project.pages.CommonSteps;
import com.project.tests.utilities.ConfigProvider;
import com.project.tests.utilities.Driver;
import com.project.tests.utilities.Log;
import com.project.utility.extentreports.ExtentConfiguration;
import com.project.utility.extentreports.ExtentTestManager;
import io.cucumber.java.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.OutputType;

import com.project.tests.utilities.SystemVsConfigProp;

import java.io.File;

public class Hooks {
    private static final Logger LOGGER = LogManager.getLogger(Hooks.class);
    private String scenarioName;
    SystemVsConfigProp systemVsConfigProp = new SystemVsConfigProp();
    private static final String BROWSER = SystemVsConfigProp.SystemPropertyVariables.Browser.name();
    private static final String ENVIRONMENT = SystemVsConfigProp.SystemPropertyVariables.Environment.name();

    private static boolean isSetUpDone = false;
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int skippedTests = 0;
    private static int failedTests = 0;

    private static String tagName = "";

    @Before
    public void beforeEachScenario(Scenario scenario) {
        scenarioName = scenario.getName();
        if (tagName.isEmpty()) {
            tagName = scenario.getSourceTagNames().toString().substring(1);
        }
        Log.info(LOGGER, "###### Scenario: [" + scenarioName + "] ######");
        if (ConfigProvider.getAsString("isExtentReportRequired").equals("true")) {
            if (!isSetUpDone) {
                Log.warning(LOGGER, "beforeEachScenario: This will run once before all scenarios to initiate the extent report");
                Log.error(LOGGER, "beforeEachScenario: This will run once before all scenarios to initiate the extent report");
                ExtentTestManager.initExtentConfiguration();

                Log.info(LOGGER, "Report start");
                isSetUpDone = true;
            }
            ExtentTestManager.startTest(scenarioName, scenarioName);
        }

        String browserName = systemVsConfigProp.getProperty(BROWSER);
        String environmentName = systemVsConfigProp.getProperty(ENVIRONMENT);

        Log.info(LOGGER, "browserName: [{}]", browserName);
        Log.info(LOGGER, "environmentName: [{}]", environmentName);
        Driver.openBrowser(browserName, scenarioName, environmentName);
        totalTests++;
    }

    @After
    public void afterEachScenario(Scenario scenario) {
        scenarioName = scenario.getName();
        try {
            String status;
            boolean isFailed = scenario.isFailed();
            if (isFailed) {
                status = "Fail";
                CommonSteps.waitForSeconds(2);
                takeScreenShot(scenarioName);
                byte[] screenshotBytes = Driver.getDriver().getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshotBytes, "image/png", scenarioName);
                failedTests++;
            } else {
                status = "Pass";
                Driver.tearDown();
                passedTests++;
            }
            Log.info(LOGGER, "!!!!! Scenario Name: [{}] Status -- [{}] !!!!!", scenarioName, status);

        } catch (Exception e) {
            Log.error(LOGGER, "Exception in Hooks @afterEachScenario: {}", e.getMessage());
            Assertions.fail("Exception in Hooks @afterEachScenario: " + e.getMessage());
        }
    }

    @AfterStep
    public void takeScreenshotAfterEachScenario(Scenario scenario) {
        scenarioName = scenario.getName();
        final byte[] screenshotBytes = Driver.getDriver().getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshotBytes, "image/png", scenarioName);
    }


    // IMPORTANT: @AfterAll works fine, ignore the message when you hover-over
    @AfterAll
    public static void afterAll() {
        if (ConfigProvider.getAsString("isExtentReportRequired").equals("true")) {
            ExtentConfiguration.getInstance().flush();
            ExtentTestManager.endTest();
        }
        Log.info(LOGGER, "Report flush.");
        Log.info(LOGGER, "[TagName] is [{}]", tagName);
        Log.info(LOGGER, "[DeploymentVersion] is [dgw536454ww6fds]");
        Log.info(LOGGER, "[TotalTests] is [{}]", totalTests);
        Log.info(LOGGER, "[PassedTests] is [{}]", passedTests);
        Log.info(LOGGER, "[SkippedTests] is [{}]", skippedTests);
        Log.info(LOGGER, "[FailedTests] is [{}]", failedTests);
        String buildStatus;
        if (failedTests > 0) {
            buildStatus = "Still Failing";
        } else {
            buildStatus = "All tests are passing";
        }
        Log.info(LOGGER, "[BuildStatus] is [{}]", buildStatus);

        try {
            String[] to = new String[]{
                    "s.gheorghe94@gmail.com",
//                    "romanzaldeawork@gmail.com ",
//                    "octavian.sdet@gmail.com"
            };
            PlaygroundTest.sendEmail(to, "What's up bud?!", "This is a test email from the automation.");
        } catch (Exception e) {
            Log.error(LOGGER, "Failed to send email with attachments in Hooks @AfterAll. Exception: " + e.getMessage());
            Assertions.fail("Failed to send email with attachments in Hooks @AfterAll. Exception: " + e.getMessage());
        }
    }

    private void takeScreenShot(String scenarioName) {
        try {
            File screenshotFile = Driver.getDriver().getScreenshotAs(OutputType.FILE);
            String pathName = System.getProperty("user.dir") + "/screen_capture/" + scenarioName + ".png";
            FileUtils.copyFile(screenshotFile, new File(pathName));
        } catch (Exception e) {
            Log.error(LOGGER, "Failed to take screenshot. Exception: " + e.getMessage());
            Assertions.fail("Failed to take screenshot. Exception: " + e.getMessage());
        }
    }
}
