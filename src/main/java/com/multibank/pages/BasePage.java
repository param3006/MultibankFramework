package com.multibank.pages;

import com.multibank.factory.DriverFactory;
import com.multibank.utils.WaitUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@Slf4j
public abstract class BasePage {

    protected final WebDriver         driver;
    protected final WebDriverWait     wait;
    protected final WebDriverWait     shortWait;
    protected final JavascriptExecutor js;
    protected final Actions           actions;

    protected BasePage() {
        this.driver    = DriverFactory.getDriver();
        this.wait      = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(6));
        this.js        = (JavascriptExecutor) driver;
        this.actions   = new Actions(driver);
        PageFactory.initElements(driver, this);
    }


    public void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
        waitForPageLoad();
    }

    public String getCurrentUrl() { return driver.getCurrentUrl(); }
    public String getPageTitle()  { return driver.getTitle(); }


    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected boolean isElementPresent(By locator) {
        try {
            shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isElementVisible(By locator) {
        try {
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    protected void type(String textToType,WebElement element){
        try{
            safeClick(element);
            waitForClickable(element);
            element.sendKeys(textToType);
            log.info("Sent keys");
        }catch (NoSuchElementException noSuchElementException){
            throw new java.util.NoSuchElementException("Element does not exist");
        }
    }


    protected void safeClick(By locator) {
        safeClick(waitForClickable(locator));
    }

    protected void safeClick(WebElement element) {
        waitForClickable(element);
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            log.warn("Normal click intercepted; falling back to JS click");
            jsClick(element);
        }
    }

    protected void jsClick(WebElement element) {
        js.executeScript("arguments[0].click();", element);
    }

    protected void scrollIntoView(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center',inline:'nearest'});", element);
        WaitUtils.waitForAnimations(driver);
    }

    protected void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        WaitUtils.waitForAnimations(driver);
    }

    protected void scrollToTop() {
        js.executeScript("window.scrollTo(0, 0);");
        WaitUtils.waitForAnimations(driver);
    }


    protected String getText(WebElement element) {
        waitForVisible(element);
        return element.getText().trim();
    }

    protected String getAttribute(WebElement element, String attr) {
        waitForVisible(element);
        return element.getAttribute(attr);
    }


    protected void waitForPageLoad() {
        wait.until(d -> js.executeScript("return document.readyState").equals("complete"));
        WaitUtils.waitForAnimations(driver);
    }

    protected void highlight(WebElement element) {
        String original = (String) js.executeScript("return arguments[0].style.border;", element);
        js.executeScript("arguments[0].style.border='3px solid red'", element);
        try { Thread.sleep(300); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        js.executeScript("arguments[0].style.border='" + original + "'", element);
    }

    public abstract boolean isPageLoaded();
}
