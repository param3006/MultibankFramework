package com.multibank.factory;

import com.multibank.config.EnvironmentConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER_POOL = new ThreadLocal<>();

    private DriverFactory() {}

    public static WebDriver createDriver() {
        String browser  = System.getProperty("browser",  EnvironmentConfig.get("browser",  "chrome")).toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", EnvironmentConfig.get("headless", "false")));
        String gridUrl  = EnvironmentConfig.get("grid.url", "");

        log.info("Creating WebDriver – browser={}, headless={}, grid={}",
                 browser, headless, gridUrl.isBlank() ? "local" : gridUrl);

        WebDriver driver = gridUrl.isBlank()
                ? createLocalDriver(browser, headless)
                : createRemoteDriver(browser, headless, gridUrl);

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        DRIVER_POOL.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER_POOL.get();
        if (driver == null) {
            throw new IllegalStateException(
                "No WebDriver found for thread [" + Thread.currentThread().getName() + "]. " +
                "Call DriverFactory.createDriver() before using getDriver().");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER_POOL.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("WebDriver quit successfully for thread [{}]",
                         Thread.currentThread().getName());
            } catch (Exception e) {
                log.warn("Exception while quitting driver: {}", e.getMessage());
            } finally {
                DRIVER_POOL.remove();
            }
        }
    }


    private static WebDriver createLocalDriver(String browser, boolean headless) {
        return switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver(buildFirefoxOptions(headless));
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                yield new EdgeDriver(buildEdgeOptions(headless));
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                yield new ChromeDriver(buildChromeOptions(headless));
            }
        };
    }


    private static WebDriver createRemoteDriver(String browser, boolean headless, String gridUrl) {
        MutableCapabilities caps = switch (browser) {
            case "firefox" -> buildFirefoxOptions(headless);
            case "edge"    -> buildEdgeOptions(headless);
            default        -> buildChromeOptions(headless);
        };
        try {
            return new RemoteWebDriver(new URL(gridUrl), caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Selenium Grid URL: " + gridUrl, e);
        }
    }

    // ── Options builders ──────────────────────────────────────────────

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions opts = new ChromeOptions();
        if (headless) opts.addArguments("--headless=new");

        opts.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-gpu",
            "--window-size=1440,900",
            "--disable-extensions",
            "--disable-infobars",
            "--disable-notifications",
            "--lang=en-US"
        );

        // Suppress automation detection
        opts.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        opts.setExperimentalOption("useAutomationExtension", false);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("credentials_enable_service", false);
        opts.setExperimentalOption("prefs", prefs);

        return opts;
    }

    private static FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions opts = new FirefoxOptions();
        if (headless) opts.addArguments("-headless");
        opts.addArguments("--width=1440", "--height=900");
        opts.addPreference("dom.webnotifications.enabled", false);
        return opts;
    }

    private static EdgeOptions buildEdgeOptions(boolean headless) {
        EdgeOptions opts = new EdgeOptions();
        if (headless) opts.addArguments("--headless=new");
        opts.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1440,900"
        );
        return opts;
    }
}
