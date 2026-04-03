package com.multibank.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.multibank.factory.DriverFactory;
import com.multibank.utils.ScreenshotUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;

import java.util.Base64;

@Slf4j
public class TestListener implements ITestListener, ISuiteListener {


    @Override
    public void onStart(ISuite suite) {
        ExtentReportManager.init();
        log.info("Suite '{}' started", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentReportManager.flush();
        log.info("Suite '{}' finished – report flushed", suite.getName());
    }


    @Override
    public void onTestStart(ITestResult result) {
        String name = result.getMethod().getMethodName();
        String desc = result.getMethod().getDescription();
        ExtentReportManager.createTest(name, desc);
        log.info("▶  Test started: {}", name);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) test.log(Status.PASS, "Test PASSED ✔");
        log.info("✔  PASS – {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.error("✘  FAIL – {}: {}", testName, result.getThrowable().getMessage());

        ExtentTest test = ExtentReportManager.getTest();
        if (test == null) return;

        test.log(Status.FAIL, result.getThrowable());

        try {
            byte[] png = ScreenshotUtils.captureAsBytes(DriverFactory.getDriver());
            if (png.length > 0) {
                String base64 = Base64.getEncoder().encodeToString(png);
                test.fail("Screenshot on failure:",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());

                ScreenshotUtils.capture(DriverFactory.getDriver(), testName);
            }
        } catch (Exception e) {
            test.warning("Screenshot capture failed: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) test.log(Status.SKIP,
            "Test SKIPPED – " + result.getThrowable().getMessage());
        log.warn("⊘  SKIP – {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }
}
