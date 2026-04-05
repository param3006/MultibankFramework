package com.multibank.tests;

import com.multibank.config.EnvironmentConfig;
import com.multibank.factory.DriverFactory;
import com.multibank.listeners.ExtentReportManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;


@Slf4j
@Listeners({
    com.multibank.listeners.TestListener.class,
    com.multibank.listeners.RetryTransformer.class
})
public abstract class BaseTest {

    private static final String BASE_URL =
        EnvironmentConfig.get("base.url", "https://trade.multibank.io");


    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        ExtentReportManager.init();
        log.info("Test suite starting – base URL: {}", BASE_URL);
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flush();
        log.info("Test suite finished");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        log.info("Setting up driver for test on thread [{}]",
                 Thread.currentThread().getName());
        DriverFactory.createDriver();
        driver().get(BASE_URL);
        log.info("Navigated to base URL: {}", BASE_URL);
    }

    @AfterMethod(enabled = false)
    public void tearDown() {
        DriverFactory.quitDriver();
        log.info("Driver quit for thread [{}]", Thread.currentThread().getName());
    }


    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

    protected String baseUrl() {
        return BASE_URL;
    }
}
