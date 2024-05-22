package com.project.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.After;
import org.openqa.selenium.remote.RemoteWebDriver;

public class StepDefinitions {
    private static final ThreadLocal<RemoteWebDriver> DRIVER = new ThreadLocal<>();

    @Given("I am on the Google homepage")
    public void i_am_on_the_google_homepage() {
        RemoteWebDriver driver = DRIVER.get();
        if (driver == null) {
            driver = new ChromeDriver();
            DRIVER.set(driver);
        }
        driver.get("https://www.google.com/");
    }

    @When("I search for {string}")
    public void i_search_for(String query) {
        RemoteWebDriver driver = DRIVER.get();
        driver.findElement(By.name("q")).sendKeys(query);
    }

    @After
    public void after() {
        RemoteWebDriver driver = DRIVER.get();
        driver.quit();
        DRIVER.remove();
    }
}