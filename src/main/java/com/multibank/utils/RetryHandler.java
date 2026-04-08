package com.multibank.utils;

import com.multibank.models.ConfigManager;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;


@Slf4j
public class RetryHandler {

    private RetryHandler() {
    }

    public static Response withRetry(Supplier<Response> action) {
        int maxRetries = ConfigManager.get().maxRetryCount();
        int delayMs = ConfigManager.get().retryDelayMs();

        Response response = null;
        for (int attempts = 1; attempts <= maxRetries + 1; attempts++) {
            response = action.get();
            int status = response.statusCode();
            if (status < 500) {
                if (attempts > 1) {
                    log.info("Retry Succeeded on attempt {}" + attempts);
                }
                return response;
            }
            log.warn("Attempts {}/{} returned status {}; retrying in {}ms...", attempts, maxRetries + 1, status, delayMs);
            if (attempts <= maxRetries) {
                sleep(delayMs);
            }
        }

        log.error("All Retries exhausted, Last status : {} ", response.statusCode());
        return response;
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
