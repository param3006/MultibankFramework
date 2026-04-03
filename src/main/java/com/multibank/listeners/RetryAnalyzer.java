package com.multibank.listeners;

import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

@Slf4j
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = Integer.getInteger("retry.count", 1);
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRIES) {
            retryCount++;
            log.warn("Retrying test '{}' – attempt {}/{}",
                result.getMethod().getMethodName(), retryCount, MAX_RETRIES);
            return true;
        }
        return false;
    }
}
