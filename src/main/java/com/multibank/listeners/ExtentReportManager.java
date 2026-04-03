package com.multibank.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.multibank.config.EnvironmentConfig;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public final class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST_THREAD = new ThreadLocal<>();

    private static final String REPORT_DIR = "reports/html/";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    private ExtentReportManager() {}

   public static synchronized void init() {
        if (extent != null) return;   // Already initialised

        String fileName = REPORT_DIR + "MultiBank_TestReport_" +
                          FMT.format(LocalDateTime.now()) + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(fileName);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("MultiBank QA Report");
        spark.config().setReportName("MultiBank Trading Platform – Automation Results");
        spark.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("Application", "trade.multibank.io");
        extent.setSystemInfo("Environment", EnvironmentConfig.get("env", "prod"));
        extent.setSystemInfo("Browser",     System.getProperty("browser", "chrome"));
        extent.setSystemInfo("Headless",    System.getProperty("headless", "false"));
        extent.setSystemInfo("OS",          System.getProperty("os.name"));
        extent.setSystemInfo("Java",        System.getProperty("java.version"));

        log.info("ExtentReports initialised → {}", fileName);
    }

    public static ExtentTest createTest(String name, String description) {
        ExtentTest test = extent.createTest(name, description);
        TEST_THREAD.set(test);
        return test;
    }

    public static ExtentTest createTest(String name) {
        return createTest(name, "");
    }

    public static ExtentTest getTest() {
        return TEST_THREAD.get();
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            log.info("ExtentReports flushed");
        }
    }
}
