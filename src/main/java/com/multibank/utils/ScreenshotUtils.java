package com.multibank.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
public final class ScreenshotUtils {

    private static final String OUTPUT_DIR = "reports/screenshots";
    private static final DateTimeFormatter TIMESTAMP_FMT =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {}
    
    public static String capture(WebDriver driver, String testName) {
        if (!(driver instanceof TakesScreenshot ts)) {
            log.warn("Driver does not support screenshots");
            return null;
        }
        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            String fileName = sanitise(testName) + "_" + TIMESTAMP_FMT.format(LocalDateTime.now()) + ".png";
            Path dest = Paths.get(OUTPUT_DIR, fileName);
            File src = ts.getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            log.info("Screenshot saved: {}", dest.toAbsolutePath());
            return dest.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to save screenshot for test '{}': {}", testName, e.getMessage());
            return null;
        }
    }

    public static byte[] captureAsBytes(WebDriver driver) {
        if (driver instanceof TakesScreenshot ts) {
            try {
                return ts.getScreenshotAs(OutputType.BYTES);
            } catch (Exception e) {
                log.warn("Could not capture screenshot bytes: {}", e.getMessage());
            }
        }
        return new byte[0];
    }

    private static String sanitise(String input) {
        return input == null ? "unknown" : input.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
