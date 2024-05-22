package com.project.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.project.pages.GoogleSearchPage;

public class GoogleSearchStepDef extends AbstractSteps {
    @Given("Load google website")
    public void loadGoogleWebsite() {
        getPage(GoogleSearchPage.class).loadPage();
    }

    @When("Search on Google")
    public void searchOnGoogle() {
        getPage(GoogleSearchPage.class).searchOnGoogle();
    }

    @Then("Verify page title")
    public void verifyPageTitle() {
        getPage(GoogleSearchPage.class).verifyPageTitle();
    }

    @Then("Verify page title failure")
    public void verifyPageTitleFailure() {
        getPage(GoogleSearchPage.class).verifyPageTitleFailure();
    }
}
