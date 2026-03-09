package com.is.infra.testng;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries failed tests up to a configurable maximum.
 * Stub — to be activated when CI flakiness is observed.
 *
 * Usage: @Test(retryAnalyzer = RetryAnalyzer.class)
 * Or: wire via AnnotationTransformer to apply to all tests automatically.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY_COUNT = 1;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        // TODO: make MAX_RETRY_COUNT configurable via ConfigManager
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            return true;
        }
        return false;
    }
}
