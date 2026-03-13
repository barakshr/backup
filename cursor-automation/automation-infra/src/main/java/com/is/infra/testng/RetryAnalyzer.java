package com.is.infra.testng;

import com.is.infra.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries failed tests up to a configurable maximum.
 *
 * Max retry count is read from config.properties key "retry.max" (default: 1).
 * Override per environment via env var RETRY_MAX.
 *
 * Wiring options:
 *   - Per-test:   @Test(retryAnalyzer = RetryAnalyzer.class)
 *   - Globally:   via a RetryAnnotationTransformer (planned — see issuesBacklog.md)
 *
 * Note: see issuesBacklog.md for the known issue with retry + dirty class-level state.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);

    private static final int MAX_RETRY_COUNT = ConfigManager.load().getInt("retry.max", 1);

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            log.warn("Retrying test '{}' (attempt {}/{})", result.getName(), retryCount, MAX_RETRY_COUNT);
            return true;
        }
        return false;
    }
}
