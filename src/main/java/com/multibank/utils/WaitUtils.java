package com.multibank.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


@Slf4j
public final class WaitUtils {

    private static final int ANIMATION_POLL_MS = 300;

    private WaitUtils() {}

    public static void waitForPageReady(WebDriver driver, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        wait.until((ExpectedCondition<Boolean>) d ->
            "complete".equals(js.executeScript("return document.readyState")));

        try {
            wait.until((ExpectedCondition<Boolean>) d -> {
                Object result = js.executeScript(
                    "return (typeof jQuery !== 'undefined') ? jQuery.active === 0 : true;");
                return Boolean.TRUE.equals(result);
            });
        } catch (Exception e) {
            log.debug("jQuery not present or AJAX wait skipped: {}", e.getMessage());
        }
    }

    public static void waitForAnimations(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long deadline = System.currentTimeMillis() + 2_000;  // 2 s max
        while (System.currentTimeMillis() < deadline) {
            try {
                Boolean animating = (Boolean) js.executeScript(
                    "var els = document.querySelectorAll('*');" +
                    "for (var i = 0; i < els.length; i++) {" +
                    "  var s = window.getComputedStyle(els[i]);" +
                    "  if (s.animationName !== 'none' || parseFloat(s.transitionDuration) > 0) return true;" +
                    "}" +
                    "return false;");
                if (!Boolean.TRUE.equals(animating)) break;
            } catch (Exception e) {
                break;  // Any JS error → stop polling
            }
            try { Thread.sleep(ANIMATION_POLL_MS); } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static boolean waitForUrlContains(WebDriver driver, String fragment, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> d.getCurrentUrl().contains(fragment));
        } catch (Exception e) {
            log.warn("URL never contained '{}'; current URL: {}", fragment, driver.getCurrentUrl());
            return false;
        }
    }

    public static boolean waitForTitleContains(WebDriver driver, String text, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> d.getTitle().toLowerCase().contains(text.toLowerCase()));
        } catch (Exception e) {
            log.warn("Title never contained '{}'; actual: '{}'", text, driver.getTitle());
            return false;
        }
    }
}
