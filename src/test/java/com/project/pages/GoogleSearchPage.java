package com.project.pages;

import com.project.tests.utilities.Driver;
import com.project.tests.utilities.Log;
import com.project.tests.utilities.SystemVsConfigProp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

public class GoogleSearchPage extends CommonSteps {
    private static final Logger LOGGER = LogManager.getLogger(GoogleSearchPage.class);
    SystemVsConfigProp systemVsConfigProp = new SystemVsConfigProp();

    private static final By searchBox = By.name("q");
    private static final By searchButton = By.name("btnK");
    private static final By shoppingHeader = By.xpath("//div[contains(text(),'Shopping')]");

    public void loadPage() {
        String url = systemVsConfigProp.getProperty(SystemVsConfigProp.SystemPropertyVariables.GOOGLEURL.name());
        Driver.getDriver().get(url);
        Log.info(LOGGER, "Successfully loaded Google page");
        String sessionID = Driver.getDriver().getSessionId().toString();
        Log.info(LOGGER, "Session ID is: [{}]", sessionID);
    }

    public void searchOnGoogle() {
        waitForPageToLoad(LOGGER);
        enterData(searchBox, "searchBox", "iPhone 15", LOGGER);
        click(searchButton, "searchButton", LOGGER);
        waitForPageToLoad(LOGGER);
        click(shoppingHeader, "shoppingHeader", LOGGER);
        Log.info(LOGGER,"Works as expected!");
//        Assertions.fail("Just fail.");
    }

    public void verifyPageTitle() {
        String actualTitle = getTitle(LOGGER);
        Assertions.assertEquals("iPhone 15 - Google Shopping", actualTitle, "Title is not the same");
    }

    public void verifyPageTitleFailure() {
        String actualTitle = getTitle(LOGGER);
        Assertions.assertEquals("Failure", actualTitle, "Title is not the same");
    }
}
