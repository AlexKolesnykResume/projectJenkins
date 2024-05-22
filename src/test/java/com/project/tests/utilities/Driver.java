package com.project.tests.utilities;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;

public class Driver {
    private Driver() {

    }

    private static final ThreadLocal<RemoteWebDriver> threadDriver = new ThreadLocal<>();
    private static final ThreadLocal<String> sessionId = new ThreadLocal<>();

    public static RemoteWebDriver getDriver() {
        return threadDriver.get();
    }

    public static void openBrowser(String browser, String scenarioName, String environment) {
        try {
            switch (browser) {
                case ("FirefoxOnLocal") -> threadDriver.set(setFireFoxDriverLocal());
                case ("ChromeOnLocal") -> threadDriver.set(setChromeDriverLocal());
                case ("EdgeOnLocal") -> threadDriver.set(setEdgeDriverLocal());
                case ("ChromeOnSauce") -> threadDriver.set(setChromeDriverSauce(browser, scenarioName, environment));
                default -> Assertions.fail("Invalid browser [" + browser + "] is selected");
            }
            sessionId.set(threadDriver.get().getSessionId().toString());
            threadDriver.get().manage().window().maximize();
            threadDriver.get().manage().deleteAllCookies();
            threadDriver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    private static RemoteWebDriver setChromeDriverLocal() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("disable-infobars");
        options.addArguments("--no-sandbox");
        options.addArguments("--use-fake-ui-for-media-stream=1");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        return new ChromeDriver(options);
    }

    private static RemoteWebDriver setFireFoxDriverLocal() {
        return new FirefoxDriver();
    }

    private static RemoteWebDriver setEdgeDriverLocal() {
        return new EdgeDriver();
    }

    public static void tearDown() {
        if (getDriver() != null) {
            getDriver().close();
            threadDriver.remove();
        }
    }

    private static RemoteWebDriver setChromeDriverSauce(String browser, String scenarioName, String environment) throws URISyntaxException, MalformedURLException {
        SystemVsConfigProp systemVsConfigProp = new SystemVsConfigProp();
        String username = systemVsConfigProp.getProperty(SystemVsConfigProp.SystemPropertyVariables.SAUCE_USERNAME.name());
        String accessKey = systemVsConfigProp.getProperty(SystemVsConfigProp.SystemPropertyVariables.SAUCE_KEY.name());
        String sauceServer = "@ondemand.us-west-1.saucelabs.com/wd/hub";
        String url = "http://" + username + ":" + accessKey + sauceServer;
        System.out.println("Sauce full URL: [" + url + "].");
        RemoteWebDriver driver = null;
        switch (browser) {
            case ("ChromeOnSauce") -> {
                String downloadFilepath = System.getProperty("user.dir") + "\\src\\test\\resources\\Downloads";
                HashMap<String, Object> chromePreferences = new HashMap<>();
                chromePreferences.put("profile.default_content_settings.popups", 0);
                chromePreferences.put("download.default_directory", downloadFilepath);

                ChromeOptions options = new ChromeOptions();
                options.setCapability(ChromeOptions.CAPABILITY, options);
                options.setCapability("platform", "Windows 10");
                options.setCapability("version", "latest");
                options.setCapability("screenResolution", "1600x1200");
                options.setCapability("parent-tunnel", "test");
                options.setCapability("tunnelIdentifier", "ProjectJenkins");
                options.setCapability("time-zone", "chicago");
                options.setCapability("name", environment + ":" + scenarioName);
                options.setCapability("maxDuration", "1800");
                options.setCapability("idleTimeout", 1000);
                options.setCapability("recordMp4", true);
                options.setExperimentalOption("preferences", chromePreferences);
                try {
                    driver = new RemoteWebDriver(new URI(url).toURL(), options);
                } catch (MalformedURLException | URISyntaxException e) {
                    Assertions.fail("Invalid SauceLabs URL: [" + url + "].");
                } catch (WebDriverException e) {
                    options.setCapability("tunnelIdentifier", "ProjectJenkins");
                    driver = new RemoteWebDriver(new URI(url).toURL(), options);
                }
            }
            case ("FirefoxOnSauce") -> {
                // TODO: Add the implementation for other drivers
            }
            default -> Assertions.fail("Invalid sauce labs browser selected [" + browser + "].");
        }
        if (driver != null) {
            driver.manage().window().maximize();
        }
        return driver;
    }
}
