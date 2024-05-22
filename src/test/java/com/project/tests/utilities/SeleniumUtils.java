package com.project.tests.utilities;

import com.project.pages.CommonSteps;
import io.cucumber.datatable.DataTable;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.NoSuchElementException;

/**
 * SeleniumUtils class contains driver object (RemoteWebDriver). This works for both Local Mode and SauceLabs
 * Driver Object is created only in this class, and we need to use the methods created in this class with Driver
 * For Local Mode use - beforeMethod("Browser", "local", "TestName"); TestName can be anything
 * For Sauce Labs use - beforeMethod("Browser", "Sauce", "TestName"); TestName can be anything
 * All reusable functions are written in this class and used in the rest of the classes
 * In case you need anything in specific, please create a method here and use this in other classes
 */
public class SeleniumUtils {
    /**
     * Waits for the presence of a WebElement identified by the given locator.
     * Uses FluentWait to handle synchronization issues, with the following configurations: <br>
     * - Timeout: 45 seconds <br>
     * - Polling Interval: 500 milliseconds <br>
     * - Ignored Exceptions: StaleElementReferenceException, ElementNotInteractableException,
     * NoSuchElementException, ElementClickInterceptedException
     *
     * @param locator The By object identifying the element.
     * @return WebElement once it is located.
     * @throws TimeoutException                 If the element is not found within the specified timeout.
     * @throws NoSuchElementException           If the element is not present in the DOM.
     * @throws ElementNotInteractableException  If the element is present but not interactable.
     * @throws StaleElementReferenceException   If the element reference is stale.
     * @throws ElementClickInterceptedException If a click could not be intercepted.
     */
    public WebElement getElement(final By locator) {
        return new FluentWait<>(Driver.getDriver())
                .withTimeout(Duration.ofSeconds(45))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementNotInteractableException.class)
                .ignoring(NoSuchElementException.class)
                .ignoring(ElementClickInterceptedException.class)
                .until((ExpectedCondition<WebElement>) driver ->
                        Objects.requireNonNull(driver, "Driver cannot be null").findElement(locator));
    }

    /**
     * Waits for the presence of a list of WebElements identified by the given locator.
     * Uses FluentWait to handle synchronization issues, with the following configurations: <br>
     * - Timeout: 45 seconds <br>
     * - Polling Interval: 500 milliseconds <br>
     * - Ignored Exceptions: StaleElementReferenceException, ElementNotInteractableException,
     * NoSuchElementException, ElementClickInterceptedException
     *
     * @param locator The By object identifying the elements.
     * @return List of WebElements once they are located.
     * @throws TimeoutException                 If the elements are not found within the specified timeout.
     * @throws NoSuchElementException           If no elements are present in the DOM.
     * @throws ElementNotInteractableException  If elements are present but not interactable.
     * @throws StaleElementReferenceException   If the element references are stale.
     * @throws ElementClickInterceptedException If a click could not be intercepted.
     */
    public List<WebElement> getElements(final By locator) {
        return new FluentWait<>(Driver.getDriver())
                .withTimeout(Duration.ofSeconds(45))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementNotInteractableException.class)
                .ignoring(NoSuchElementException.class)
                .ignoring(ElementClickInterceptedException.class)
                .until((ExpectedCondition<List<WebElement>>) driver ->
                        Objects.requireNonNull(driver, "Driver cannot be null").findElements(locator));
    }

    // IMPORTANT: This method should be used only for By elements that are initialized as class variables
//    private String getFieldName(By locator, Class<?> clazz) {
//        String fieldNameFinal = null;
//        Map<String, Object> allFieldNameValue = new HashMap<>();
//        try {
//            Constructor<?> constructor = clazz.getDeclaredConstructor();
//            Field[] fields = clazz.getDeclaredFields();
//            for (Field field : fields) {
//                String fieldName = field.getName();
//                field.setAccessible(true);
//                Object fieldValue = field.get(constructor.newInstance());
//                allFieldNameValue.put(fieldName, fieldValue);
//            }
//        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//
//        for (Map.Entry<String, Object> entry: allFieldNameValue.entrySet()) {
//            if (entry.getValue() == null) {
//                continue;
//            }
//            String fieldValue = entry.getValue().toString();
//            if (fieldValue.equals(locator.toString())) {
//                fieldNameFinal = entry.getKey();
//                break;
//            }
//        }
//        return fieldNameFinal;
//    }
//
//    public void click(By locator, Class<?> clazz) {
//        String elementName = getFieldName(locator, clazz);
//        try {
//            getElement(locator).click();
//            LOGGER.info("Clicked " + elementName);
//        } catch (Exception e) {
//            Assertions.fail("Could not click on the element " + elementName + " due to exception: " + e);
//        }
//    }

    /**
     * Clicks on a WebElement identified by the given locator and visually highlights the element before the click.
     *
     * @param locator     The By object identifying the element to click.
     * @param elementName A descriptive name for the element being clicked, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @throws AssertionError If the click operation fails due to any exception.
     *                        The error message includes details about the element and the exception that occurred.
     */
    public void click(By locator, String elementName, Logger logger) {
        try {
            highlightElement(locator, elementName, logger);
            WebElement element = getElement(locator);
            element.click();
            Log.info(logger, "Clicked [{}] element", elementName);
            if (isDisplayed(element)) {
                unhighlightElement(locator, elementName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Could not click on the element [%s]. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Highlights a WebElement identified by the given locator by applying a red border using JavaScript.
     *
     * @param locator     The By object identifying the element to highlight.
     * @param elementName A descriptive name for the element being highlighted, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @throws AssertionError If the highlighting operation fails due to any exception.
     *                        The error message includes details about the element and the exception that occurred.
     */
    private void highlightElement(By locator, String elementName, Logger logger) {
        try {
            JavascriptExecutor js = Driver.getDriver();
            js.executeScript("arguments[0].style.border='3px solid red'", getElement(locator));
            logger.debug("Highlighted the element [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Cannot highlight the element [%s]. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Highlights a WebElement by applying a red border using JavaScript.
     *
     * @param element     The WebElement to highlight.
     * @param elementName A descriptive name for the element being highlighted, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @throws AssertionError If the highlighting operation fails due to any exception.
     *                        The error message includes details about the element and the exception that occurred.
     */
    private void highlightElement(WebElement element, String elementName, Logger logger) {
        try {
            JavascriptExecutor js = Driver.getDriver();
            js.executeScript("arguments[0].style.border='3px solid red'", element);
            logger.debug("Highlighted the element [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Cannot highlight the element [%s]. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Removes the highlighting of a WebElement identified by the given locator
     * by clearing the border style using JavaScript.
     *
     * @param locator     The By object identifying the element to unhighlight.
     * @param elementName A descriptive name for the element being unhighlighted, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @throws AssertionError If the unhighlighting operation fails due to any exception.
     *                        The error message includes details about the element and the exception that occurred.
     */
    private void unhighlightElement(By locator, String elementName, Logger logger) {
        try {
            JavascriptExecutor js = Driver.getDriver();
            js.executeScript("arguments[0].style.border=''", getElement(locator));
            logger.info("Removed the highlight of the element [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Cannot remove the highlight of the element [%s]. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Removes the highlighting of a WebElement by clearing the border style using JavaScript.
     *
     * @param element     The WebElement to unhighlight.
     * @param elementName A descriptive name for the element being unhighlighted, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @throws AssertionError If the unhighlighting operation fails due to any exception.
     *                        The error message includes details about the element and the exception that occurred.
     */
    private void unhighlightElement(WebElement element, String elementName, Logger logger) {
        try {
            JavascriptExecutor js = Driver.getDriver();
            js.executeScript("arguments[0].style.border=''", element);
            logger.info("Removed the highlight of the element [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Cannot remove the highlight of the element [%s]. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Clears the data from a form field identified by the given locator,
     * and visually highlights the field before and after the action.
     *
     * @param locator   The By object identifying the form field to clear.
     * @param fieldName A descriptive name for the field being cleared, used for logging and error messages.
     * @param logger    The logger to record information and errors.
     * @throws AssertionError If the field clearing operation fails due to any exception.
     *                        The error message includes details about the field and the exception that occurred.
     */
    public void clearData(By locator, String fieldName, Logger logger) {
        try {
            highlightElement(locator, fieldName, logger);
            getElement(locator).clear();
            Log.info(logger, "Field [{}] has been cleared successfully", fieldName);
            unhighlightElement(locator, fieldName, logger);
        } catch (Exception e) {
            String errorMessage = String.format("Unable to clear the field [%s]. Exception: %s",
                    fieldName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Enters data into a form field identified by the given locator.
     * Clears existing data, highlights the field before and after entering data.
     *
     * @param locator   The By object identifying the form field to enter data into.
     * @param fieldName A descriptive name for the field, used for logging and error messages.
     * @param text      The text to enter into the field.
     * @param logger    The logger to record information and errors.
     * @throws AssertionError If the data entry operation fails due to any exception.
     *                        The error message includes details about the field and the exception that occurred.
     */
    public void enterData(By locator, String fieldName, String text, Logger logger) {
        try {
            clearData(locator, fieldName, logger);
            highlightElement(locator, fieldName, logger);
            Log.info(logger, "Entering the text [{}] in the field [{}]", text, fieldName);
            getElement(locator).sendKeys(text);
            unhighlightElement(locator, fieldName, logger);
        } catch (Exception e) {
            String errorMessage = String.format("Unable to enter data in the field [%s]. Exception: %s",
                    fieldName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Retrieves the value of a text box using JavaScript and returns it as a String,
     * and highlights the text box before and after getting the text.
     *
     * @param locator     The By object identifying the text box element.
     * @param textBoxName A descriptive name for the text box, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @return The value of the text box or null if an error occurs.
     * @throws AssertionError If there is an issue while retrieving the value from the text box.
     *                        The error message includes details about the text box and the exception that occurred.
     */
    public String getValueOfJavaScriptTextBox(By locator, String textBoxName, Logger logger) {
        String textBoxValue = null;
        try {
            highlightElement(locator, textBoxName, logger);
            WebElement element = getElement(locator);
            textBoxValue = Driver.getDriver()
                    .executeScript("return arguments[0].value", element).toString();
            Log.info(logger, "The value of the text box [{}] is [{}]", textBoxName, textBoxValue);
            if (isDisplayed(element)) {
                unhighlightElement(locator, textBoxName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Unable to read the text from the text box [%s]. Exception: %s",
                    textBoxName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
        return textBoxValue;
    }

    /**
     * Navigates to the specified application URL using WebDriver's get() method.
     *
     * @param applicationUrl     The URL of the application to navigate to.
     * @param applicationUrlName A descriptive name for the application URL, used for logging and error messages.
     * @param logger             The logger to record information and errors.
     * @throws AssertionError If the navigation to the application URL fails due to any exception.
     *                        The error message includes details about the application URL
     *                        and the exception that occurred.
     */
    public void getToUrl(String applicationUrl, String applicationUrlName, Logger logger) {
        try {
            Driver.getDriver().get(applicationUrl);
            Log.info(logger, "Navigated to [{}] - [{}]", applicationUrlName, applicationUrl);
        } catch (Exception e) {
            String errorMessage = String.format("Unable to navigate to the application URL [%s] - [%s]. Exception: %s",
                    applicationUrlName, applicationUrl, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Navigates to the specified application URL using WebDriver's navigate().to() method.
     *
     * @param applicationUrl     The URL of the application to navigate to.
     * @param applicationUrlName A descriptive name for the application URL, used for logging and error messages.
     * @param logger             The logger to record information and errors.
     * @throws AssertionError If the navigation to the application URL fails due to any exception.
     *                        The error message includes details about the application URL
     *                        and the exception that occurred.
     */
    public void navigateToUrl(String applicationUrl, String applicationUrlName, Logger logger) {
        try {
            Driver.getDriver().navigate().to(applicationUrl);
            Log.info(logger, "Navigated to [{}] - [{}]", applicationUrlName, applicationUrl);
        } catch (Exception e) {
            String errorMessage = String.format("Unable to navigate to the application URL [%s] - [%s]. Exception: %s",
                    applicationUrlName, applicationUrl, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Retrieves and logs the coordinates of a WebElement identified by the given locator,
     * and highlights the element before and remove the highlight after logging the coordinates.
     *
     * @param locator     The By object identifying the element.
     * @param elementName A descriptive name for the element, used for logging.
     * @param logger      The logger to record information.
     * @throws AssertionError If there is an issue while retrieving or logging the coordinates,
     *                        an assertion error is thrown with a detailed error message.
     */
    public void logElementCoordinates(By locator, String elementName, Logger logger) {
        try {
            highlightElement(locator, elementName, logger);
            WebElement element = getElement(locator);
            Point coordinates = element.getLocation();
            Log.info(logger, "Coordinates of the element [{}] - (x:[{}], y:[{}])",
                    elementName, coordinates.getX(), coordinates.getY());
            unhighlightElement(locator, elementName, logger);
        } catch (Exception e) {
            String errorMessage = String.format("Error retrieving coordinates of the element [%s] - [%s]: %s",
                    elementName, locator, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Selects an option from a dropdown identified by the given locator using visible text,
     * and highlights the dropdown before selecting an option and remove the highlight after an option is selected,
     * and the dropdown element is still visible.
     *
     * @param locator     The By object identifying the dropdown element.
     * @param visibleText The visible text of the option to be selected.
     * @param logger      The logger to record information and errors.
     * @throws AssertionError If the selection operation fails due to any exception.
     *                        The error message includes details about the dropdown and the exception that occurred.
     */
    public void selectByVisibleText(By locator, String dropdownName, String visibleText, Logger logger) {
        try {
            highlightElement(locator, dropdownName, logger);
            WebElement element = getElement(locator);
            Select dropdown = new Select(element);
            dropdown.selectByVisibleText(visibleText);
            Log.info(logger, "Selected option by visible text [{}] from the dropdown [{}]",
                    visibleText, dropdownName);
            if (isDisplayed(element)) {
                unhighlightElement(locator, dropdownName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Unable to select option by visible text [%s] from the dropdown [%s]." +
                    " Exception: %s", visibleText, dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Selects an option from a dropdown WebElement using visible text,
     * and highlights the dropdown before selecting an option and remove the highlight after an option is selected,
     * and the dropdown element is still visible.
     *
     * @param element      The WebElement representing the dropdown.
     * @param dropdownName A descriptive name for the dropdown, used for logging and error messages.
     * @param visibleText  The visible text of the option to be selected.
     * @param logger       The logger to record information and errors.
     * @throws AssertionError If the selection operation fails due to any exception.
     *                        The error message includes details about the dropdown, the selected text,
     *                        and the exception that occurred.
     */
    public void selectByVisibleText(WebElement element, String dropdownName, String visibleText, Logger logger) {
        try {
            highlightElement(element, dropdownName, logger);
            Select dropdown = new Select(element);
            dropdown.selectByVisibleText(visibleText);
            Log.info(logger, "Selected option by visible text [{}] from the dropdown [{}]",
                    visibleText, dropdownName);
            if (isDisplayed(element)) {
                unhighlightElement(element, dropdownName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Unable to select option by visible text [%s] from the dropdown [%s]." +
                    " Exception: %s", visibleText, dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Selects an option from a dropdown identified by the given locator using the option's 'value' attribute,
     * and highlights the dropdown before selecting an option and remove the highlight after an option is selected,
     * and the dropdown element is still visible.
     *
     * @param locator      The By object identifying the dropdown element.
     * @param dropdownName A descriptive name for the dropdown, used for logging and error messages.
     * @param value        The value attribute of the option to be selected.
     * @param logger       The logger to record information and errors.
     * @throws AssertionError If the selection operation fails due to any exception.
     *                        The error message includes details about the dropdown, the selected value,
     *                        and the exception that occurred.
     */
    public void selectByValue(By locator, String dropdownName, String value, Logger logger) {
        try {
            highlightElement(locator, dropdownName, logger);
            WebElement element = getElement(locator);
            Select dropdown = new Select(element);
            dropdown.selectByValue(value);
            Log.info(logger, "Selected option by value [{}] from the dropdown [{}]",
                    value, dropdownName);
            if (isDisplayed(element)) {
                unhighlightElement(locator, dropdownName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Unable to select option by value [%s] from the dropdown [%s]. " +
                    "Exception: %s", value, dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Selects an option from a dropdown WebElement using the option's 'value' attribute,
     * and highlights the dropdown before selecting an option and remove the highlight after an option is selected,
     * and the dropdown element is still visible.
     *
     * @param element      The WebElement representing the dropdown.
     * @param dropdownName A descriptive name for the dropdown, used for logging and error messages.
     * @param value        The value attribute of the option to be selected.
     * @param logger       The logger to record information and errors.
     * @throws AssertionError If the selection operation fails due to any exception.
     *                        The error message includes details about the dropdown, the selected value,
     *                        and the exception that occurred.
     */
    public void selectByValue(WebElement element, String dropdownName, String value, Logger logger) {
        try {
            highlightElement(element, dropdownName, logger);
            Select dropdown = new Select(element);
            dropdown.selectByValue(value);
            Log.info(logger, "Selected option by value [{}] from the dropdown [{}]",
                    value, dropdownName);
            if (isDisplayed(element)) {
                unhighlightElement(element, dropdownName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Unable to select option by value [%s] from the dropdown [%s]. " +
                    "Exception: %s", value, dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Selects an option from a dropdown identified by the given locator using the option's index,
     * and highlights the dropdown before selecting an option and remove the highlight after an option is selected,
     * and the dropdown element is still visible.
     *
     * @param locator      The By object identifying the dropdown.
     * @param dropdownName A descriptive name for the dropdown, used for logging and error messages.
     * @param indexString  The index of the option to be selected as a string.
     * @param logger       The logger to record information and errors.
     * @throws AssertionError If the selection operation fails due to any exception.
     *                        The error message includes details about the dropdown, the selected index,
     *                        and the exception that occurred.
     */
    public void selectByIndex(By locator, String dropdownName, String indexString, Logger logger) {
        try {
            highlightElement(locator, dropdownName, logger);
            WebElement element = getElement(locator);
            int indexInteger = Integer.parseInt(indexString);
            Select dropdown = new Select(element);
            dropdown.selectByIndex(indexInteger);
            Log.info(logger, "Selected option by the index {} from dropdown [{}]",
                    indexString, dropdownName);
            if (isDisplayed(element)) {
                unhighlightElement(locator, dropdownName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Unable to select option by index [%s] from the dropdown [%s]. " +
                    "Exception: %s", indexString, dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Selects an option from a dropdown WebElement using the option's index,
     * and highlights the dropdown before selecting an option and remove the highlight after an option is selected,
     * and the dropdown element is still visible.
     *
     * @param element      The WebElement representing the dropdown.
     * @param dropdownName A descriptive name for the dropdown, used for logging and error messages.
     * @param indexString  The index of the option to be selected as a string.
     * @throws AssertionError If the selection operation fails due to any exception.
     *                        The error message includes details about the dropdown, the selected index,
     *                        and the exception that occurred.
     */
    public void selectByIndex(WebElement element, String dropdownName, String indexString, Logger logger) {
        try {
            highlightElement(element, dropdownName, logger);
            int indexInteger = Integer.parseInt(indexString);
            Select dropdown = new Select(element);
            dropdown.selectByIndex(indexInteger);
            Log.info(logger, "Selected option by the index {} from dropdown [{}]",
                    indexString, dropdownName);
            if (isDisplayed(element)) {
                unhighlightElement(element, dropdownName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Unable to select option by index [%s] from the dropdown [%s]. " +
                    "Exception: %s", indexString, dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Selects an option from a dropdown identified by the given locator using the option's index,
     * and highlights the dropdown before selecting an option and remove the highlight after an option is selected,
     * and the dropdown element is still visible.
     *
     * @param locator      The By object identifying the dropdown.
     * @param dropdownName A descriptive name for the dropdown, used for logging and error messages.
     * @param index        The index of the option to be selected.
     * @param logger       The logger to record information and errors.
     * @throws AssertionError If the selection operation fails due to any exception.
     *                        The error message includes details about the dropdown, the selected index,
     *                        and the exception that occurred.
     */
    public void selectByIndex(By locator, String dropdownName, int index, Logger logger) {
        try {
            highlightElement(locator, dropdownName, logger);
            WebElement element = getElement(locator);
            Select dropdown = new Select(element);
            dropdown.selectByIndex(index);
            Log.info(logger, "Selected option by the index {} from dropdown [{}]",
                    index, dropdownName);
            Log.info(logger, "Select the index {} from dropdown", index);
            if (isDisplayed(element)) {
                unhighlightElement(locator, dropdownName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Unable to select option by index [%s] from the dropdown [%s]. " +
                    "Exception: %s", index, dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Selects an option from a dropdown identified by the given WebElement using the option's index,
     * and highlights the dropdown before selecting an option and remove the highlight after an option is selected,
     * and the dropdown element is still visible.
     *
     * @param element      The WebElement identifying the dropdown.
     * @param dropdownName A descriptive name for the dropdown, used for logging and error messages.
     * @param index        The index of the option to be selected.
     * @param logger       The logger to record information and errors.
     * @throws AssertionError If the selection operation fails due to any exception.
     *                        The error message includes details about the dropdown, the selected index,
     *                        and the exception that occurred.
     */
    public void selectByIndex(WebElement element, String dropdownName, int index, Logger logger) {
        try {
            highlightElement(element, dropdownName, logger);
            Select dropdown = new Select(element);
            dropdown.selectByIndex(index);
            Log.info(logger, "Selected option by the index {} from dropdown [{}]", index, dropdownName);
            if (isDisplayed(element)) {
                unhighlightElement(element, dropdownName, logger);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Unable to select option by index [%s] from the dropdown [%s]. " +
                    "Exception: %s", index, dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Gets the value of a specified attribute for the WebElement identified by the given locator.
     *
     * @param locator   The By object identifying the element.
     * @param attribute The attribute whose value is to be retrieved.
     * @param logger    The logger to record information and errors.
     * @return The value of the specified attribute, or "No Attribute Found" if the attribute is not found.
     * @throws AssertionError If an exception occurs while retrieving the attribute value.
     *                        The error message includes details about the element, attribute,
     *                        and the exception that occurred.
     */
    public String getAttribute(By locator, String elementName, String attribute, Logger logger) {
        try {
            highlightElement(locator, elementName, logger);
            WebElement element = getElement(locator);
            Log.info(logger, "Getting the attribute [{}] for the element [{}]", attribute, elementName);
            unhighlightElement(locator, elementName, logger);
            return element.getAttribute(attribute);
        } catch (Exception e) {
            String errorMessage = String.format("Attribute [%s] not found for the element [%s]. Exception: %s",
                    attribute, elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return "No Attribute Found";
        }
    }

    /**
     * Checks if a WebElement identified by the given locator is selected.
     *
     * @param locator     The By object identifying the element.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @return true if the element is selected, false otherwise.
     * @throws AssertionError If an exception occurs during the check.
     *                        The error message includes details about the element and the exception.
     */
    public boolean isSelected(By locator, String elementName, Logger logger) {
        boolean status = false;
        try {
            highlightElement(locator, elementName, logger);
            status = getElement(locator).isSelected();
            Log.info(logger, "[{}] is selected: [{}]", elementName, status);
            unhighlightElement(locator, elementName, logger);
        } catch (Exception e) {
            String errorMessage = String.format("Unable to check if element [%s] is selected. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
        return status;
    }

    /**
     * Retrieves the text of an element identified by the given locator.
     *
     * @param locator     The By object identifying the element to retrieve text from.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @return The text of the element or a default message if an error occurs.
     * @throws AssertionError If the text retrieval operation fails due to any exception.
     *                        The error message includes details about the element and the exception that occurred.
     */
    public String getText(By locator, String elementName, Logger logger) {
        String text;
        try {
            highlightElement(locator, elementName, logger);
            text = getElement(locator).getText();
            Log.info(logger, "Text of element [{}] is: [{}]", elementName, text);
            unhighlightElement(locator, elementName, logger);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to get text from element [%s]. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            text = "Unable to get String text";
        }
        return text;
    }

    /**
     * Waits for an element identified by the given locator to be clickable.
     *
     * @param locator The By object identifying the element to wait for.
     * @param timeout The maximum time to wait for the element to be clickable.
     * @param logger  The logger to record information and errors.
     */
    public void explicitlyClickableWait(By locator, String elementName, Duration timeout, Logger logger) {
        try {
            WebDriverWait webDriverWait = new WebDriverWait(Driver.getDriver(), timeout);
            webDriverWait.until(ExpectedConditions.elementToBeClickable(locator));
            Log.info(logger, "Element [{}] is clickable", elementName);
        } catch (TimeoutException e) {
            String errorMessage = String.format("Timeout waiting for element [%s] to be clickable. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Performs a keyboard input with the Alt key pressed and released along with the specified key.
     *
     * @param inputKey The key to be pressed and released along with Alt.
     * @param logger   The logger to record information and errors.
     */
    public void altKeyboardInput(String inputKey, Logger logger) {
        try {
            Actions keyAction = new Actions(Driver.getDriver());
            keyAction.keyDown(Keys.ALT).keyDown(Keys.SHIFT)
                    .sendKeys(inputKey).keyUp(Keys.ALT).keyUp(Keys.SHIFT).perform();
            Log.info(logger, "Successfully performed Alt+Shift+{} keyboard input", inputKey);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to perform Alt+Shift+%s keyboard input. Exception: %s",
                    inputKey, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Waits for the visibility of an element identified by the given locator.
     *
     * @param locator     The By object identifying the element to wait for.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     */
    public void expectedVisibilityWait(By locator, String elementName, Logger logger) {
        try {
            WebDriverWait webDriverWait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(30));
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            Log.info(logger, "Element [{}] is present and visible on the page", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Element [%s] is not found or not visible on the page. " +
                    "Exception: %s", elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Checks if an element identified by the given locator is editable by attempting to clear it.
     *
     * @param locator     The By object identifying the element to check for editability.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param expected    The expected editability status (true for editable, false for read-only).
     * @param logger      The logger to record information and errors.
     * @return true if the element is editable as expected, false otherwise.
     */
    public boolean isEditable(By locator, String elementName, boolean expected, Logger logger) {
        boolean actualStatus = false;
        try {
            getElement(locator).clear();
            Log.info(logger, "Element [{}] is not in read-only form", elementName);
            actualStatus = true;
        } catch (InvalidElementStateException e) {
            // Catch InvalidElementStateException if the element is disabled or in read-only form
            Log.error(logger, "Element is disabled or in read-only form");
        }
        return expected == actualStatus;
    }

    /**
     * Checks if an element identified by the given locator is editable by attempting to clear it.
     *
     * @param locator     The By object identifying the element to check for editability.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @return true if the element is editable, false otherwise.
     */
    public boolean isEditable(By locator, String elementName, Logger logger) {
        boolean isEditable = false;
        try {
            getElement(locator).clear();
            Log.info(logger, "Element [{}] is editable", elementName);
            isEditable = true;
        } catch (InvalidElementStateException e) {
            Log.error(logger, "Element [{}] is disabled or in a read-only state", elementName);
        }
        return isEditable;
    }

    /**
     * Performs a drag-and-drop operation from a source element to a target element.
     *
     * @param sourceLocator The By object identifying the source element to drag.
     * @param targetLocator The By object identifying the target element to drop onto.
     * @param logger        The logger to record information and errors.
     */
    public void dragAndDrop(By sourceLocator, By targetLocator, Logger logger) {
        try {
            WebElement sourceElement = getElement(sourceLocator);
            WebElement targetElement = getElement(targetLocator);
            Actions actions = new Actions(Driver.getDriver());
            actions.dragAndDrop(sourceElement, targetElement).build().perform();
            Log.info(logger, "Performed drag-and-drop from [{}] to [{}]", sourceLocator, targetLocator);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to perform drag-and-drop from [%s] to [%s]. Exception: %s",
                    sourceLocator, targetLocator, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Checks if an element identified by the given locator is displayed on the web page.
     * Highlights the element before and remove the highlight after the check.
     *
     * @param locator     The By object identifying the element to check for display.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @return true if the element is displayed, false otherwise.
     * @throws AssertionError If an exception occurs during the check.
     *                        The error message includes details about the element and the exception.
     */
    public boolean isDisplayed(By locator, String elementName, Logger logger) {
        boolean status = false;
        try {
            highlightElement(locator, elementName, logger);
            status = getElement(locator).isDisplayed();
            logger.info("Element [{}] is displayed", elementName);
            unhighlightElement(locator, elementName, logger);
        } catch (Exception e) {
            String errorMessage = String.format("Element [%s] is not displayed or not found. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
        return status;
    }

    /**
     * Checks if an element identified by the given locator is displayed on the web page
     * without highlighting and without logging.
     *
     * @param element The WebElement to check for display.
     * @return true if the element is displayed, false otherwise.
     */
    private boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies if an element identified by the given locator is enabled.
     *
     * @param locator     The By object identifying the element to check for enablement.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @return true if the element is enabled, false otherwise.
     */
    public boolean isEnabled(By locator, String elementName, Logger logger) {
        boolean isEnabled = false;
        try {
            WebElement element = getElement(locator);
            isEnabled = element.isEnabled();
            String logMessage = String.format("Element [%s] is [%s]", elementName, isEnabled ? "enabled" : "disabled");
            Log.info(logger, logMessage);
        } catch (Exception e) {
            String errorMessage = String.format("Element [%s] not found. Exception: %s", elementName, e.getMessage());
            Log.error(logger, errorMessage);
        }
        return isEnabled;
    }

    /**
     * Verifies if an element identified by the given locator is enabled.
     *
     * @param locator     The By object identifying the element to check for enablement.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     */
    public void verifyElementIsEnabled(By locator, String elementName, Logger logger) {
        try {
            WebElement element = getElement(locator);
            if (element.isEnabled()) {
                String successMsg = String.format("Element [%s] is enabled on the page", elementName);
                Log.info(logger, successMsg);
            } else {
                String failureMsg = String.format("Element [%s] is expected to be enabled, but it is not on the page.",
                        elementName);
                Assertions.fail(failureMsg);
            }
        } catch (Exception e) {
            String errorMsg = String.format("Error while verifying element '%s' enablement: %s",
                    elementName, e.getMessage());
            Assertions.fail(errorMsg);
        }
    }

    /**
     * Verifies if an element identified by the given locator is not enabled.
     *
     * @param locator     The By object identifying the element to check for disablement.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     */
    public void verifyElementIsNotEnabled(By locator, String elementName, Logger logger) {
        try {
            WebElement element = getElement(locator);
            if (!element.isEnabled()) {
                String successMsg = String.format("Element [%s] is not enabled on the page.", elementName);
                Log.info(logger, successMsg);
            } else {
                String failureMsg = String.format("Element [%s] is expected not to be enabled, but it is on the page.",
                        elementName);
                Assertions.fail(failureMsg);
            }
        } catch (Exception e) {
            String errorMsg = String.format("Failed to verifying element [%s] is not enable on the page. Exception: %s",
                    elementName, e.getMessage());
            Assertions.fail(errorMsg);
        }
    }

    /**
     * Checks if the specified text is present in the dropdown identified by the given locator.
     *
     * @param locator      The By object identifying the dropdown.
     * @param dropdownName A descriptive name for the dropdown, used for logging and error messages.
     * @param expectedText The text to be checked in the dropdown.
     * @param logger       The logger to record information and errors.
     * @return true if the text is present in the dropdown, false otherwise.
     */
    public boolean isTextPresentInDropdown(By locator, String dropdownName, String expectedText, Logger logger) {
        try {
            Select select = new Select(getElement(locator));
            List<WebElement> options = select.getOptions();
            for (WebElement option : options) {
                String actualText = option.getText().trim();
                if (actualText.equals(expectedText)) {
                    Log.info(logger, "Text value [{}] is present in the dropdown [{}]",
                            expectedText, dropdownName);
                    return true;
                }
            }
            Log.info(logger, "Text value [{}] is NOT present in the dropdown [{}]",
                    expectedText, dropdownName);
            return false;
        } catch (Exception e) {
            String errorMsg = String.format("Error while checking text presence in dropdown [%s]. Exception: %s",
                    dropdownName, e.getMessage());
            Log.error(logger, errorMsg);
            Assertions.fail(errorMsg);
            return false;
        }
    }

    /**
     * Quits the WebDriver instance.
     */
    public void quit(Logger logger) {
        try {
            Driver.getDriver().quit();
            Log.info(logger, "WebDriver instance has been closed");
        } catch (Exception e) {
            String errorMsg = "Failed to close the WebDriver instance. Exception: " + e.getMessage();
            Log.error(logger, errorMsg);
            Assertions.fail(errorMsg);
        }
    }

    /**
     * Captures a screenshot and saves it to the specified path with a filename based on the method name and timestamp.
     *
     * @param path       The path to save the screenshot.
     * @param methodName The name of the method associated with the screenshot.
     */
    public void captureScreenshot(String path, String methodName, Logger logger) {
        try {
            File screenshotFile = Driver.getDriver().getScreenshotAs(OutputType.FILE);
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyhmmss");
            String timestamp = dateFormat.format(currentDate);
            String screenshotPath = String.format("%s\\Screenshot\\%s\\%s - %s.jpeg", path, methodName,
                    methodName, timestamp);
            org.apache.commons.io.FileUtils.copyFile(screenshotFile, new File(screenshotPath));
            Log.info(logger, "Screenshot captured and saved at: {}", screenshotPath);
        } catch (IOException e) {
            String errorMsg = "Failed to capture and save screenshot. Error: " + e.getMessage();
            Log.error(logger, errorMsg);
            Assertions.fail(errorMsg);
        }
    }

    /**
     * Scrolls the page vertically down by 200 pixels using JavaScript.
     *
     * @param logger The logger to record information and errors.
     * @throws AssertionError If an exception occurs during the scroll operation.
     *                        The error message includes details about the exception.
     */
    public void javaScriptScrollDown200(Logger logger) {
        try {
            Driver.getDriver().executeScript("scroll(0, 200)");
            Log.info(logger, "Scrolled the page vertically down by 200 pixels");
        } catch (Exception e) {
            String errorMsg = "Failed to scroll down using JavaScript. Exception: " + e.getMessage();
            Log.error(logger, errorMsg);
            Assertions.fail(errorMsg);
        }
    }

    /**
     * Scrolls the page vertically up by 200 pixels using JavaScript.
     *
     * @param logger The logger to record information and errors.
     * @throws AssertionError If an exception occurs during the scroll operation.
     *                        The error message includes details about the exception.
     */
    public void javaScriptScrollUp200(Logger logger) {
        try {
            Driver.getDriver().executeScript("scroll(-200, 0)");
            Log.info(logger, "Scrolled the page vertically up by 200 pixels");
        } catch (Exception e) {
            String errorMsg = "Failed to scroll up using JavaScript. Exception: " + e.getMessage();
            Log.error(logger, errorMsg);
            Assertions.fail(errorMsg);
        }
    }

    /**
     * Clicks on an element identified by the given locator using JavaScript execution.
     * This method uses JavaScript to perform a click on the element identified by the provided locator.
     * If any exception occurs during the click operation, an error message is logged, and the test is marked as failed.
     *
     * @param locator     The By object identifying the element to click.
     * @param elementName A descriptive name for the element being clicked, used for logging and error messages.
     * @param logger      The logger to record information and errors.
     * @throws AssertionError If an exception occurs during the click operation.
     *                        The error message includes details about the element and the exception.
     */
    public void clickByJS(By locator, String elementName, Logger logger) {
        try {
            WebElement element = getElement(locator);
            Driver.getDriver().executeScript("arguments[0].click();", element);
            Log.info(logger, "Clicked [{}] element using JavaScript", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Unable to click '%s' using JavaScript. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Switches to a frame identified by its name.
     * This method uses the frame name to switch to the corresponding frame.
     * If the frame is not found within the specified wait time, an error message is logged,
     * and the test is marked as failed.
     *
     * @param frameName The name of the frame to switch to.
     * @param logger    The logger to record information and errors.
     * @throws AssertionError If the frame is not found within the specified wait time.
     */
    public void switchFrameWithFrameName(String frameName, Logger logger) {
        try {
            WebDriverWait webDriverWait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(45));
            webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name(frameName)));
            Log.info(logger, "Switched to frame [{}]", frameName);
        } catch (Exception e) {
            String errorMessage = String.format("Unable to switch to frame [%s]. Exception: %s", frameName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Switches to a frame identified by its index.
     * This method uses the frame index to switch to the corresponding frame.
     * If the frame is not found within the specified wait time, an error message is logged, and the test is marked as failed.
     *
     * @param frameIndex The index of the frame to switch to.
     * @param logger     The logger to record information and errors.
     * @throws AssertionError If the frame is not found within the specified wait time.
     */
    public void switchFrameWithFrameNumber(int frameIndex, Logger logger) {
        try {
            WebElement iframeElement = getElement(By.tagName("iframe"));
            Log.info(logger, "Number of frame tags found: [{}]", iframeElement);
            WebDriverWait webDriverWait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(45));
            webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIndex));
        } catch (Exception e) {
            String errorMessage = String.format("Unable to switch to frame [%s]. Exception: %s",
                    frameIndex, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Checks if an alert is present.
     *
     * @param logger The logger to record information and errors.
     * @return true if an alert is present, false otherwise.
     */
    public boolean isAlertPresent(Logger logger) {
        try {
            Driver.getDriver().switchTo().alert();
            Log.info(logger, "Alert is present");
            return true;
        } catch (NoAlertPresentException e) {
            Log.info(logger, "Alert is NOT present.");
            return false;
        } catch (Exception e) {
            String errorMessage = String.format("Failed while checking for alert presence. " +
                    "Exception: %s", e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return false;
        }
    }

    /**
     * Accepts the alert if it is present.
     *
     * @param isAlertPresent Flag indicating whether an alert is present.
     * @param logger         The logger to record information and errors.
     */
    public void alertAccept(boolean isAlertPresent, Logger logger) {
        try {
            if (isAlertPresent) {
                Alert alert = Driver.getDriver().switchTo().alert();
                String alertText = alert.getText();
                Log.info(logger, "Alert is present with text: [{}]", alertText);
                alert.accept();
                Log.info(logger, "Accepted the alert.");
                Driver.getDriver().switchTo().defaultContent();
            } else {
                Log.info(logger, "Alert is not present");
            }
        } catch (NoAlertPresentException e) {
            Log.info(logger, "No alert found to accept");
        } catch (Exception e) {
            String errorMessage = String.format("Failed to accept alert. Exception: %s", e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Gets the text from the alert. If an alert is present, retrieves the text from it;
     * otherwise, logs a message indicating the absence of an alert.
     *
     * @param logger The logger to record information and errors.
     * @return The text from the alert, or null if no alert is present or an exception occurs.
     */
    public String getTextFromAlert(Logger logger) {
        String alertText = null;
        try {
            Alert alert = Driver.getDriver().switchTo().alert();
            alertText = alert.getText();
            Log.info(logger, "Alert text: [{}]", alertText);
        } catch (NoAlertPresentException e) {
            Log.info(logger, "No alert found to get text");
        } catch (Exception e) {
            String errorMessage = "Failed to get text from alert. Exception: " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
        return alertText;
    }

    /**
     * Dismisses an alert if it is present. If an alert is present, logs its text,
     * dismisses it, and switches back to the default content. If no alert is present,
     * logs a message indicating the absence of an alert.
     *
     * @param isAlertPresent A boolean indicating whether an alert is expected to be present.
     * @param logger         The logger to record information and errors.
     * @throws AssertionError if an unexpected exception occurs during the dismissal of the alert.
     *                        The error message will contain details about the exception.
     */
    public void alertDismiss(boolean isAlertPresent, Logger logger) {
        try {
            if (isAlertPresent) {
                Log.info(logger, "Alert Text: " + Driver.getDriver().switchTo().alert().getText());
                Driver.getDriver().switchTo().alert().dismiss();
                Driver.getDriver().switchTo().defaultContent();
            } else {
                Log.info(logger, "Alert is not present");
            }
        } catch (Exception e) {
            String errorMessage = "Exception in alertDismiss method: " + e.getMessage();
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Waits for the specified number of windows to be present before continuing execution.
     * The method waits up to 45 seconds for the given number of windows to be reached.
     *
     * @param numberOfWindows The expected number of windows.
     * @param logger          The logger to record information and errors.
     * @throws AssertionError if the expected number of windows is not reached within the specified timeout.
     *                        The error message will contain details about the exception.
     */
    public void explicitWaitForWindows(int numberOfWindows, Logger logger) {
        try {
            WebDriverWait webDriverWait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(45));
            webDriverWait.until(ExpectedConditions.numberOfWindowsToBe(numberOfWindows));
        } catch (Exception e) {
            String errorMessage = String.format("Failed while waiting for [%s]  windows. " +
                    "Expected number of windows not reached. Exception: %s", numberOfWindows, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Gets the count of all elements with the specified tag name within the scope of the given locator.
     *
     * @param locator The By object identifying the parent element.
     * @param tagName The tag name of the elements to count.
     * @param logger  The logger to record information and errors.
     * @return The count of elements with the specified tag name, or 0 if an error occurs.
     */
    public int getCountOfAllElementsWithTagName(By locator, String tagName, Logger logger) {
        try {
            List<WebElement> elements = getElement(locator).findElements(By.tagName(tagName));
            int count = elements.size();
            Log.info(logger, "Number of elements with tag [{}] found is: [{}]", tagName, count);
            return count;
        } catch (Exception e) {
            Log.error(logger, "Failed to find elements with locator: [{}]. Exception: {}",
                    locator, e.getMessage());
            return 0;
        }
    }

    /**
     * Gets the title of the current page.
     *
     * @param logger The logger to record information and errors.
     * @return The title of the page, or an error message if unable to retrieve it.
     */
    public String getTitle(Logger logger) {
        try {
            String title = Driver.getDriver().getTitle();
            Log.info(logger, "Page title is: [{}]", title);
            return title;
        } catch (Exception e) {
            Log.error(logger, "Unable to get the page title");
            Assertions.fail("Unable to get the page title. Error: " + e.getMessage());
            return "Unable to get the page title.";
        }
    }

    /**
     * Switches between window handles, logs window titles, and returns a list of window handles.
     *
     * @param logger The logger for logging information.
     * @return List of window handles.
     */
    // TODO: The method name might need to be changed to getWindowHandles
    public ArrayList<String> switchWindowHandle(Logger logger) {
        String window1;
        String window2;
        ArrayList<String> windows = new ArrayList<>();
        Set<String> windowHandles = Driver.getDriver().getWindowHandles();
        Log.info(logger, "Number of windows is: [{}]", windowHandles.size());
        Iterator<String> iterator = windowHandles.iterator();
        while (iterator.hasNext()) {
            window1 = iterator.next();
            window2 = iterator.next();
            String windowTitle1 = Driver.getDriver().switchTo().window(window1).getTitle();
            String windowTitle2 = Driver.getDriver().switchTo().window(window2).getTitle();
            Log.info(logger, "Window1 title: [{}]", windowTitle1);
            Log.info(logger, "Window2 title: [{}]", windowTitle2);
            windows.add(window1);
            windows.add(window2);
        }
        return windows;
    }

    /**
     * Switches to the specified window using its handle.
     *
     * @param window The handle of the window to switch to.
     * @param logger The logger for logging information.
     */
    public void switchToWindow(String window, Logger logger) {
        try {
            Driver.getDriver().switchTo().window(window);
            Log.info(logger, "Switched to window with handle: [{}]", window);
        } catch (NoSuchWindowException e) {
            String errorMessage = String.format("Failed to switch to window with handle: %s. Window not found. " +
                    "Exception: %s", window, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Switches to the main window.
     *
     * @param logger The logger for logging information.
     */
    public void switchToMainWindow(Logger logger) {
        try {
            ArrayList<String> windows = switchWindowHandle(logger);
            if (!windows.isEmpty()) {
                String mainWindowHandle = windows.get(0);
                Driver.getDriver().switchTo().window(mainWindowHandle);
                Log.info(logger, "Switched to the main window with handle: [{}]", mainWindowHandle);
            } else {
                String errorMessage = "No windows found to switch to. Main window not found.";
                Log.error(logger, errorMessage);
                Assertions.fail(errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Failed to switch to the main window. Exception: " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Retrieves the X-coordinate of the element.
     *
     * @param locator     The locator of the element.
     * @param elementName The name or description of the element (for logging purposes).
     * @param logger      The logger for logging information.
     * @return The X-coordinate of the element.
     */
    public int getXCoordinate(By locator, String elementName, Logger logger) {
        try {
            WebElement element = getElement(locator);
            int xCoordinate = element.getLocation().getX();
            Log.info(logger, "X-coordinate of element [{}] is: [{}]", elementName, xCoordinate);
            return xCoordinate;
        } catch (Exception e) {
            String errorMessage = "Failed to retrieve X-coordinate of element [" + elementName + "]. " +
                    "Exception: " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return 0;
        }
    }

    /**
     * Retrieves the Y-coordinate of the element.
     *
     * @param locator The locator of the element.
     * @return The Y-coordinate of the element.
     */
    public int getYCoordinate(By locator, String elementName, Logger logger) {
        try {
            WebElement element = getElement(locator);
            int yCoordinate = element.getLocation().getY();
            Log.info(logger, "Y-coordinate of element [{}] is: [{}]", elementName, yCoordinate);
            return yCoordinate;
        } catch (Exception e) {
            String errorMessage = "Failed to retrieve Y-coordinate of element [" + elementName + "]. " +
                    "Exception: " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return 0;
        }
    }

    /**
     * Checks if the specified attribute is present in the element.
     *
     * @param locator     The locator of the element.
     * @param elementName The name of the element for logging.
     * @param attribute   The attribute to check.
     * @return true if the attribute is present, false otherwise.
     */
    public boolean isAttributePresent(By locator, String elementName, String attribute, Logger logger) {
        try {
            WebElement element = getElement(locator);
            String attributeValue = element.getAttribute(attribute);
            Log.info(logger, "Attribute [{}] of element [{}] is present with value: [{}]",
                    attribute, elementName, attributeValue);
            if (attributeValue != null && !attributeValue.equalsIgnoreCase("null")) {
                return true;
            } else {
                String warningMessage = String.format("Attribute [%s] of element [%s] is not present", attribute, elementName);
                Log.warning(logger, warningMessage);
                return false;
            }
        } catch (Exception e) {
            String errorMessage = String.format("Failed checking attribute [%s] of element [%s]. Exception: %s",
                    attribute, elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return false;
        }
    }

    /**
     * Checks if a horizontal scroll bar is present on the page.
     *
     * @param logger The logger to record log messages.
     * @return True if a horizontal scroll bar is present, false otherwise.
     */
    public boolean isHorizontalScrollBarPresent(Logger logger) {
        try {
            String horizontalScript = "return document.documentElement.scrollWidth>document.documentElement.clientWidth;";
            boolean horizontalScrollStatus = (Boolean) Driver.getDriver().executeScript(horizontalScript);
            Log.info(logger, "Horizontal scroll bar is present: [{}]", horizontalScrollStatus);
            return horizontalScrollStatus;
        } catch (Exception e) {
            String errorMessage = String.format("Failed to check for the presence of horizontal scroll bar. " +
                    "Exception: %s", e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return false;
        }
    }

    /**
     * Checks if a vertical scroll bar is present on the page.
     *
     * @param logger The logger to record log messages.
     * @return True if a vertical scroll bar is present, false otherwise.
     */
    public boolean isVerticalScrollBarPresent(Logger logger) {
        try {
            String verticalScript = "return document.documentElement.scrollHeight>document.documentElement.clientHeight;";
            Boolean verticalScrollStatus = (Boolean) Driver.getDriver().executeScript(verticalScript);
            Log.info(logger, "Vertical scroll bar is present: [{}]", verticalScrollStatus);
            return verticalScrollStatus;
        } catch (Exception e) {
            String errorMessage = String.format("Failed to check for the presence of vertical scroll bar. " +
                    "Exception: %s", e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return false;
        }
    }

    /**
     * Switches the WebDriver focus to the default content.
     * Logs success or failure messages.
     */
    public void switchToDefault(Logger logger) {
        try {
            Driver.getDriver().switchTo().defaultContent();
            Log.info(logger, "Switched to default content");
        } catch (Exception e) {
            String errorMessage = "Failed to switch to default content. Error: " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Retrieves all descendant elements matching the specified property under the given parent element.
     *
     * @param parentLocator     Locator for the parent element.
     * @param parentElementName Name of the parent element for logging purposes.
     * @param childLocator      Locator for the descendant elements.
     * @param childElementName  Name of the child element for logging purposes.
     * @param logger            Logger to record log messages.
     * @return A list of WebElement representing all descendant elements with the specified property.
     */
    public List<WebElement> getAllDescendantElements(By parentLocator, String parentElementName,
                                                     By childLocator, String childElementName, Logger logger) {
        try {
            WebElement rootWebElement = getElement(parentLocator);
            List<WebElement> children = rootWebElement.findElements(childLocator);
            Log.info(logger, "Found [{}] descendant elements with the specified property " +
                    "under the parent element [{}]", children.size(), parentElementName);
            return children;
        } catch (Exception e) {
            String errorMessage = String.format("Failed to get descendant elements. Parent element: [%s]. " +
                    "Child element: [%s]. Exception: %s", parentElementName, childElementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return null;
        }
    }

    /**
     * Moves the mouse pointer to the specified element.
     *
     * @param locator     Locator for the target element.
     * @param elementName Name of the target element for logging purposes.
     * @param logger      Logger to record log messages.
     */
    public void moveToElement(By locator, String elementName, Logger logger) {
        try {
            Actions action = new Actions(Driver.getDriver());
            WebElement element = getElement(locator);
            action.moveToElement(element).build().perform();
            Log.info(logger, "Moved mouse pointer to the element: [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to move mouse pointer to the element [%s]. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Checks if the element is present and matches the expected status.
     * Logs success or failure messages.
     *
     * @param locator     By object representing the element's locator.
     * @param elementName Name of the element for logging purposes.
     * @param logger      Logger for logging messages.
     * @return true if the actual status matches the expected status, false otherwise.
     */
    public boolean isElementPresent(By locator, String elementName, Logger logger) {
        boolean actualStatus = false;
        try {
            getElement(locator);
            actualStatus = true;
            Log.info(logger, "Element [{}] with locator [{}] is present as expected",
                    elementName, locator);
        } catch (Exception e) {
            Log.info(logger, "Element [{}] with locator [{}] is not present as expected",
                    elementName, locator);
        }
        return actualStatus;
    }

    /**
     * Sets the value of an input field using JavaScript.
     *
     * @param locator     The locator of the input field.
     * @param elementName The name or description of the input field (for logging purposes).
     * @param keyToSend   The value to be set in the input field.
     * @param logger      Logger for recording log messages.
     */
    public void sendKeysUsingJavaScript(By locator, String elementName, String keyToSend, Logger logger) {
        try {
            WebElement textBox = getElement(locator);
            Driver.getDriver().executeScript("arguments[0].value='" + keyToSend + "';", textBox);
            Log.info(logger, "Set value [{}] using JavaScript for element [{}]", keyToSend, elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to set value [%s] using JavaScript for element [%s]. " +
                    "Exception: %s", keyToSend, elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Clears the value of an input field using JavaScript.
     *
     * @param locator     The locator of the input field.
     * @param elementName The name or description of the input field (for logging purposes).
     * @param logger      Logger for recording log messages.
     */
    public void clearUsingJavaScript(By locator, String elementName, Logger logger) {
        try {
            WebElement element = getElement(locator);
            Driver.getDriver().executeScript("arguments[0].value='';", element);
            Log.info(logger, "Cleared value for element [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to clear value for element [%s]. " +
                    "Exception: %s", elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    public void clearUsingJavaScript(By Locator) {
        try {
            WebElement element = getElement(Locator);
            Driver.getDriver().executeScript("arguments[0].value='';", element);
        } catch (Exception e) {
            Assertions.fail("Element Not Found " + Locator + " | Error - " + e);
        }
    }

    /**
     * Gets the tag name of the specified element.
     *
     * @param locator     The locator of the element.
     * @param elementName The name or description of the element (for logging purposes).
     * @param logger      Logger for recording log messages.
     * @return The tag name of the element, or null if the element is not found.
     */
    public String getTagName(By locator, String elementName, Logger logger) {
        try {
            String tagName = getElement(locator).getTagName();
            Log.info(logger, "Tag name for element [{}] is [{}]", elementName, tagName);
            return tagName;
        } catch (Exception e) {
            String errorMessage = String.format("Failed to get tag name for element [%s]. " +
                    "Exception: %s", elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return null;
        }
    }

    /**
     * Gets the CSS value of the specified property for the element.
     *
     * @param locator      The locator of the element.
     * @param elementName  The name or description of the element (for logging purposes).
     * @param propertyName The name of the CSS property.
     * @param logger       Logger for recording log messages.
     * @return The CSS value of the specified property for the element, or null if the element is not found.
     */
    public String getCssValue(By locator, String elementName, String propertyName, Logger logger) {
        try {
            WebElement element = getElement(locator);
            String cssValue = element.getCssValue(propertyName);
            Log.info(logger, "CSS value of property [{}] for element [{}] is [{}]",
                    propertyName, elementName, cssValue);
            return cssValue;
        } catch (Exception e) {
            String errorMessage = String.format("Failed to get CSS value for property [%s] of element [%s]. " +
                    "Exception: %s", propertyName, elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return null;
        }
    }

    /**
     * Gets the window handle of the current window.
     *
     * @param logger The logger object to log messages.
     * @return The window handle of the current window, or "Window Not Found" if an exception occurs.
     */
    public String getWindowHandle(Logger logger) {
        try {
            String windowHandle = Driver.getDriver().getWindowHandle();
            Log.info(logger, "Window handle retrieved successfully: [{}]", windowHandle);
            return windowHandle;
        } catch (Exception e) {
            String errorMessage = "Failed to get window handle. Exception: " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return "Window Not Found";
        }
    }

    /**
     * Switches to a window based on its position among the available window handles.
     *
     * @param windowPosition The position of the window handle to switch to (1-indexed).
     * @param logger         Logger for recording log messages.
     */
    public void switchToWindowByPosition(int windowPosition, Logger logger) {
        try {
            int counter = 1;
            for (String handle : Driver.getDriver().getWindowHandles()) {
                if (counter == windowPosition) {
                    Driver.getDriver().switchTo().window(handle);
                    Log.info(logger, "Switched to window at position [{}]. Window handle: [{}]",
                            windowPosition, handle);
                    return;  // Exit the loop once the desired window is found and switched to.
                }
                counter++;
            }
            // If the loop completes without finding the desired window, fail the test.
            Assertions.fail("Window not found at position [" + windowPosition + "]");
        } catch (Exception e) {
            String errorMessage = String.format("Failed to switch to window at position [%s]. " +
                    "Exception: %s", windowPosition, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Performs a double click on the specified element.
     *
     * @param locator     The locator of the element to double-click.
     * @param elementName The name or description of the element (for logging purposes).
     * @param logger      Logger for recording log messages.
     */
    public void performDoubleClick(By locator, String elementName, Logger logger) {
        try {
            Actions action = new Actions(Driver.getDriver());
            action.moveToElement(getElement(locator)).doubleClick().perform();
            Log.info(logger, "Double-clicked on element [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to double-click on element [%s]. " +
                    "Exception: %s", elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Scrolls to the element using JavaScript.
     *
     * @param locator     By object representing the element's locator.
     * @param elementName A descriptive name for the element.
     * @param logger      Logger object for logging messages.
     */
    public void scrollToElementByJS(By locator, String elementName, Logger logger) {
        try {
            WebElement element = getElement(locator);
            Driver.getDriver().executeScript("arguments[0].scrollIntoView(true);", element);
            Log.info(logger, "Scrolled to element [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to scroll to element [%s]. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Performs a mouse hover action on an element.
     *
     * @param locator     By object representing the element's locator.
     * @param elementName A descriptive name for the element.
     * @param logger      Logger object for logging messages.
     */
    public void performMouseHover(By locator, String elementName, Logger logger) {
        try {
            Actions action = new Actions(Driver.getDriver());
            action.moveToElement(getElement(locator)).build().perform();
            Log.info(logger, "Mouse hovered over [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Mouse hover over failed on [%s]. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Performs a mouse hover with holding action on an element.
     *
     * @param locator     By object representing the element's locator.
     * @param elementName A descriptive name for the element.
     * @param logger      Logger object for logging messages.
     */
    public void mouseHoldHover(By locator, String elementName, Logger logger) {
        try {
            Actions action = new Actions(Driver.getDriver());
            WebElement element = getElement(locator);
            action.clickAndHold().moveToElement(element);
            action.moveToElement(element).build().perform();
            Log.info(logger, "Performed mouse hold hover on element [{}]", elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to perform mouse hold hover on element [%s]. " +
                    "Exception: %s", elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Performs a mouse hover over an element using JavaScript.
     *
     * @param locator     By object representing the element's locator.
     * @param elementName A descriptive name for the element.
     * @param logger      Logger object for logging messages.
     */
    public void mouseHoverOverByJS(By locator, String elementName, Logger logger) {
        String mouseOverScript = "if (document.createEvent) {var evObj = document.createEvent('MouseEvents'); " +
                "evObj.initEvent('mouseover', true, false); arguments[0].dispatchEvent(evObj);} " +
                "else if (document.createEventObject) {arguments[0].fireEvent('onmouseover');}";
        try {
            Driver.getDriver().executeScript(mouseOverScript, getElement(locator));
            Log.info(logger, "Mouse hovered over element '{}' with locator: [{}]", elementName, locator);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to hover over element [%s] using JavaScript. Exception: %s",
                    elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Checks if a dropdown element is disabled.
     *
     * @param locator      The By locator of the dropdown element.
     * @param dropdownName The name or identifier of the dropdown (for logging purposes).
     * @param logger       The logger to log messages.
     * @return True if the dropdown is disabled, false otherwise.
     */
    public boolean isDropdownDisabled(By locator, String dropdownName, Logger logger) {
        try {
            WebElement dropdown = getElement(locator);
            String disabledAttribute = dropdown.getAttribute("disabled");
            if (disabledAttribute != null && disabledAttribute.equalsIgnoreCase("true")) {
                Log.info(logger, "Dropdown [{}] is Disabled", dropdownName);
                return true;
            } else {
                Log.info(logger, "Dropdown [{}] is Enabled", dropdownName);
                return false;
            }
        } catch (Exception e) {
            String errorMessage = "Error checking dropdown status for [" + dropdownName + "]: " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return false;
        }
    }

    /**
     * Waits for the visibility of either of the two elements.
     *
     * @param firstLocator  The By locator for the first element.
     * @param secondLocator The By locator for the second element.
     * @param logger        The Logger object for logging.
     */
    public void waitForEitherElementVisible(By firstLocator, By secondLocator, Logger logger) {
        try {
            mainLoop:
            for (int attempt = 0; attempt < 6; attempt++) {
                for (int waitTime = 0; waitTime < 45; waitTime++) {
                    if (isElementDisplayed(firstLocator)) {
                        Log.info(logger, "waitForEitherElementVisible: Found the first element");
                        break mainLoop;
                    }
                    if (isElementDisplayed(secondLocator)) {
                        Log.info(logger, "waitForEitherElementVisible: Found the second element");
                        break mainLoop;
                    }
                    CommonSteps.waitForSeconds(3);
                }
            }
        } catch (Exception e) {
            String errorMessage = String.format("Failed to wait for both elements. Exception: %s", e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Checks if an element is displayed on the page.
     *
     * @param locator The By locator of the element.
     * @return True if the element is displayed, false otherwise.
     */
    private boolean isElementDisplayed(By locator) {
        try {
            return getElement(locator).isDisplayed();
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    /**
     * Retrieves the text content of a hidden element using JavaScript.
     *
     * @param locator     The By locator of the hidden element.
     * @param elementName The name or description of the element for logging purposes.
     * @param logger      The Logger object for logging.
     * @return The text content of the hidden element.
     */
    public String getHiddenElementsTextWithJavaScript(By locator, String elementName, Logger logger) {
        String hiddenText = null;
        try {
            WebElement element = getElement(locator);
            hiddenText = (String) Driver.getDriver()
                    .executeScript("return arguments[0].textContent;", element);
            Log.info(logger, "Retrieved text content of the hidden element [{}]: [{}]",
                    elementName, hiddenText);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to retrieve text content of the hidden element [%s]. " +
                    "Exception: %s", elementName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
        return hiddenText;
    }

    /**
     * Gets the current URL of the web page.
     *
     * @return The current URL as a string, or null if an exception occurs.
     */
    public String getCurrentURL(Logger logger) {
        try {
            String url = Driver.getDriver().getCurrentUrl();
            Log.info(logger, "Current URL is: [{}]", url);
            return url;
        } catch (Exception e) {
            String errorMessage = String.format("Failed to get current URL. Exception: %s", e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return null;
        }
    }

    /**
     * Gets the visible text of the first selected option in a dropdown.
     *
     * @param locator The locator of the dropdown element.
     * @return The visible text of the first selected option.
     */
    public String getTextOfSelectOptionInDropdown(By locator, String dropdownName, Logger logger) {
        try {
            Select select = new Select(getElement(locator));
            WebElement option = select.getFirstSelectedOption();
            String selectedText = option.getText();
            Log.info(logger, "Selected text of dropdown [{}] is: [{}]", dropdownName, selectedText);
            return selectedText;
        } catch (Exception e) {
            String errorMessage = String.format("Failed to retrieve selected option' text from dropdown [%s]. " +
                    "Exception: %s", dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return "Unable to get the String";
        }
    }

    /**
     * Retrieves the value attribute of the currently selected option in a dropdown.
     *
     * @param locator      The By locator for the dropdown element.
     * @param dropdownName A descriptive name for the dropdown element.
     * @param logger       The Logger object for logging messages.
     * @return The value attribute of the selected option, or null if an exception occurs.
     */
    public String getValueOfSelectedOptionInDropdown(By locator, String dropdownName, Logger logger) {
        String selectedOptionValue = null;
        try {
            Select select = new Select(getElement(locator));
            selectedOptionValue = select.getFirstSelectedOption().getAttribute("value");
            Log.info(logger, "Selected option from dropdown [{}] has value: [{}]",
                    dropdownName, selectedOptionValue);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to retrieve selected option's value from dropdown [%s]. " +
                    "Exception: %s", dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
        return selectedOptionValue;
    }

    // TODO: Move this method to the needed class

    /**
     * Extracts data from a Cucumber DataTable and returns it as a flat list of strings.
     *
     * @param table The Cucumber DataTable containing the data.
     * @return A List of strings representing the flattened data.
     */
    public List<String> getData(DataTable table) {
        List<String> info = new ArrayList<>();
        List<List<String>> dataFromGherkin = table.asLists();
        for (List<String> row : dataFromGherkin) {
            info.addAll(row);
        }
        return info;
    }

    /**
     * Retrieves the text of all options, including blank options, from a dropdown.
     *
     * @param locator      The By object identifying the dropdown element.
     * @param dropdownName The name of the dropdown element for logging purposes.
     * @param logger       The logger to record information and errors.
     * @return A List containing the text of all options. If retrieval is unsuccessful,
     * the list contains an error message.
     */
    public List<String> getAllElementsFromDropdownWithBlank(By locator, String dropdownName, Logger logger) {
        List<String> allElementsText = new ArrayList<>();
        try {
            Select dropdown = new Select(getElement(locator));
            List<WebElement> allElements = dropdown.getOptions();
            Log.info(logger, "Size of all options in dropdown [{}] is: [{}]",
                    dropdownName, allElements.size());
            for (WebElement element : allElements) {
                String optionText = element.getText();
                allElementsText.add(optionText);
            }
            Log.info(logger, "Size of all the options including blank in the dropdown [{}] is: [{}]",
                    dropdownName, allElementsText.size());
        } catch (Exception e) {
            String errorMessage = String.format("Unable to get text for all non-blank options from the dropdown [%s]." +
                    " Exception: %s", dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            allElementsText.add("Unable to get Text for all Options");
            return Collections.singletonList(errorMessage);
        }
        return allElementsText;
    }

    /**
     * Retrieves the text of all non-blank options from a dropdown.
     *
     * @param locator      The By object identifying the dropdown element.
     * @param dropdownName A descriptive name for the dropdown, used for logging and error messages.
     * @param logger       The logger to record information and errors.
     * @return A list of strings containing the text of all non-blank options, or an error message if unable to retrieve them.
     */
    public List<String> getAllElementsFromDropdownWithoutBlank(By locator, String dropdownName, Logger logger) {
        List<String> allElementsText = new ArrayList<>();
        try {
            List<WebElement> allElements;
            Select select = new Select(getElement(locator));
            allElements = select.getOptions();
            Log.info(logger, "Size of all options in the dropdown [{}] is [{}]",
                    dropdownName, allElements.size());
            for (WebElement element : allElements) {
                String text = element.getText().trim();
                if (!text.isEmpty()) {
                    allElementsText.add(text);
                }
            }
            Log.info(logger, "Size of all non-blank options in the dropdown [{}] is: [{}]",
                    dropdownName, allElementsText.size());
        } catch (Exception e) {
            String errorMessage = String.format("Unable to get text for all non-blank options from the dropdown [%s]. " +
                    "Exception: %s", dropdownName, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail();
            allElementsText.add(errorMessage);
        }
        return allElementsText;
    }

    /**
     * Refreshes the current page and handles any alert present after the refresh.
     *
     * @param logger The logger to record information and errors.
     */
    public void refreshPage(Logger logger) {
        try {
            Driver.getDriver().navigate().refresh();
            Log.info(logger, "Page refreshed successfully");
            boolean isAlertPresent = isAlertPresent(logger);
            alertAccept(isAlertPresent, logger);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to refresh the page. Exception: %s", e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Gets the background color of the element identified by the given locator.
     *
     * @param locator The By object identifying the element.
     * @return The background color of the element in hexadecimal format (#RRGGBB).
     * Returns null if the element is not found or an error occurs.
     */
    public String getElementBackgroundColor(By locator, String elementName, Logger logger) {
        try {
            WebElement element = getElement(locator);
            String color = element.getCssValue("background-color");
            String[] rgbValues = color.replace("rgba(", "")
                    .replace(")", "").split(",");
            int red = Integer.parseInt(rgbValues[0].trim());
            int green = Integer.parseInt(rgbValues[1].trim());
            int blue = Integer.parseInt(rgbValues[2].trim());
            String hexadecimalColor = String.format("#%02x%02x%02x", red, green, blue);
            Log.info(logger, "Element [{}] has hexadecimal color [{}]", elementName, hexadecimalColor);
            return hexadecimalColor;
        } catch (Exception e) {
            String errorMessage = String.format("Failed to get background color for element [%s]. Exception: %s",
                    locator, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
            return null;
        }
    }

    /**
     * Waits for the page to be fully loaded by checking the document ready state.
     */
    public void waitForPageToLoad(Logger logger) {
        try {
            new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(60))
                    .until(driver -> ((JavascriptExecutor) driver)
                            .executeScript("return document.readyState").equals("complete"));
            Log.info(logger, "Page has been fully loaded");
        } catch (Exception e) {
            String errorMessage = "Error while waiting for the page to load: " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Performs a file upload using the provided file path.
     * This method simulates the process of copying the file path to the clipboard and pasting it using
     * the Robot class. It then triggers the Enter key to complete the file upload.
     *
     * @param filePath The path of the file to be uploaded.
     */
    public void fileUpload(String filePath) {
        Robot robot = getRobot(filePath);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        CommonSteps.waitForSeconds(1);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    @NotNull
    private static Robot getRobot(String filePath) {
        Robot robot;
        try {
            StringSelection stringSelection = new StringSelection(filePath);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, stringSelection);
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e.getMessage());
        }
        return robot;
    }

    /**
     * Deletes the file named "document.pdf" from the user's Downloads directory if it exists.
     *
     * @param logger The logger to record information and errors.
     */
    public void deleteFile(Logger logger) {
        String fileName = "document.pdf";
        File file = new File(System.getProperty("user.home") + "\\Downloads\\" + fileName);

        if (file.exists()) {
            try {
                if (file.delete()) {
                    Log.info(logger, "Deleted existing file: {}", fileName);
                } else {
                    Log.warning(logger, "Failed to delete file: {}", fileName);
                }
            } catch (SecurityException e) {
                Log.error(logger, "Security exception while deleting file: {}", e.getMessage());
                Assertions.fail("Failed to delete file due to security exception.");
            }
        } else {
            Log.info(logger, "{} does not exist. No deletion needed", fileName);
        }
    }

    /**
     * Validates the content of a PDF document downloaded locally.
     *
     * @param logger The logger to record information and errors.
     */
    public void pdfValidationFromLocal(Logger logger) {
        PDFTextStripper pdfStripper = null;
        PDDocument pdDocument = null;
        COSDocument cosDocument = null;
        String parsedText = null;
        String pdfFileName = "document.pdf";
        try {
            File file = new File(System.getProperty("user.home") + "\\Downloads\\" + pdfFileName);
            RandomAccessBuffer randomAccessBuffer = new RandomAccessBuffer(new FileInputStream(file));
            PDFParser parser = new PDFParser(randomAccessBuffer);
            parser.parse();
            cosDocument = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(2);
            pdDocument = new PDDocument(cosDocument);
            parsedText = pdfStripper.getText(pdDocument);
        } catch (Exception e) {
            String errorMessage = "Failed to parse the PDF document. Exception: " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        } finally {
            try {
                if (cosDocument != null) {
                    cosDocument.close();
                }
                if (pdDocument != null) {
                    pdDocument.close();
                }
            } catch (IOException e) {
                Log.error(logger, "Failed while closing PDF documents. Exception: {}", e.getMessage());
            }
        }
        Log.info(logger, parsedText);
    }

    /**
     * Switches to a window with the given title.
     *
     * @param requiredWindowTitle The title of the window to switch to.
     * @param logger              The logger to record information and errors.
     */
    public void switchWindowWithGivenTitle(String requiredWindowTitle, Logger logger) {
        try {
            boolean flag = false;
            for (String handle : Driver.getDriver().getWindowHandles()) {
                String title = getTitle(logger);
                Driver.getDriver().switchTo().window(handle);
                Log.info(logger, "Available title is [{}]", title);
                if (title.contains(requiredWindowTitle)) {
                    Log.info(logger, "Driver switched to window with title [{}]", requiredWindowTitle);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                String errorMessage = String.format("Window with title [%s] not found", requiredWindowTitle);
                Log.error(logger, errorMessage);
                Assertions.fail(errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Failed while switching to window with title '" + requiredWindowTitle + "'. " +
                    "Exception - " + e.getMessage();
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Waits until the specified URL is loaded in the browser.
     *
     * @param url      The expected URL to wait for.
     * @param urlName  A human-readable name for the URL, used for logging purposes.
     * @param logger   The logger object to log messages.
     */
    public void waitUntilURLLoad(String url, String urlName, Logger logger) {
        try {
            WebDriverWait webDriverWait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(60));
            webDriverWait.until(ExpectedConditions.urlToBe(url));
            Log.info(logger, "The website [{}] with the expected URL [{}] is loaded", urlName, url);
        } catch (Exception e) {
            String errorMessage = String.format("Failed while waiting for the website [%s] with the URL [%s] to load." +
                    " Exception: %s", urlName, url, e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Verifies that the text present in the element identified by the given locator matches the expected text.
     *
     * @param locator      The By object identifying the element to check for text.
     * @param expectedText The expected text to match against.
     * @param logger       The logger to record information and errors.
     * @throws AssertionError If the actual and expected text do not match.
     *                        The error message includes details about the actual and expected text.
     */
    public void verifyTextIsPresent(By locator, String expectedText, Logger logger) {
        try {
            WebElement element = getElement(locator);
            String actualText = element.getText().trim();
            if (actualText.contains(expectedText)) {
                Log.info(logger, "Verified text is displayed as: [{}]", actualText);
            } else {
                String errorMessage = String.format("Text verification failed. " +
                        "Expected text: [%s] does not match actual text: '%s'", expectedText, actualText);
                Log.error(logger, errorMessage);
                Assertions.fail(errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Failed while verifying text. Exception: %s", e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Waits until the specified text is present in the element identified by the given locator.
     *
     * @param locator     The By object identifying the element.
     * @param elementName A descriptive name for the element, used for logging and error messages.
     * @param text        The text to wait for in the element.
     * @param logger      The logger to record information and errors.
     * @throws AssertionError If the text is not present within the specified timeout period.
     */
    public void waitUntilTextAppears(By locator, String elementName, String text, Logger logger) {
        try {
            WebElement element = getElement(locator);
            WebDriverWait webDriverWait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(60));
            webDriverWait.until(ExpectedConditions.textToBePresentInElement(element, text));
            Log.info(logger, "Text [{}] is present in element [{}]", text, elementName);
        } catch (Exception e) {
            String errorMessage = String.format("Exception occurred while waiting for the text [%s] to be present " +
                    "in element [%s]. Exception: %s", text, elementName, e.getMessage());
            Assertions.fail(errorMessage);
            Log.error(logger, errorMessage);
        }
    }

    /**
     * Reads and returns the content of the "pom.xml" file as a string representation of the Model.
     *
     * @param logger The logger to record information and errors.
     * @return A string representation of the Model from the "pom.xml" file.
     * @throws AssertionError If an exception occurs during the read operation.
     *                        The error message includes details about the exception.
     */
    public static String readPomProperty(Logger logger) {
        Model model = new Model();
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            model = reader.read(new FileReader("pom.xml"));
        } catch (IOException | XmlPullParserException e) {
            String errorMessage = String.format("Failed to read pom.xml file. Exception:  %s", e.getMessage());
            Log.error(logger, errorMessage);
            Assertions.fail(errorMessage);
        }
        return model.toString();
    }
}
