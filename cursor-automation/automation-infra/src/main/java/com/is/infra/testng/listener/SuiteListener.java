package com.is.infra.testng.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Suite-level lifecycle listener.
 *
 * onStart:
 *   - Logs suite name, parallel mode and thread count.
 *   - Extension point for: environment validation, driver binary setup,
 *     Feature Store health check, or any other pre-suite preparation.
 *
 * onFinish:
 *   - Logs pass/fail/skip totals across all tests in the suite.
 *   - Extension point for: custom report flush, shared resource cleanup,
 *     HTTP connection pool shutdown.
 *   - Allure report generation itself is handled by the Allure Maven plugin
 *     (post-integration-test phase) — no manual flush needed here.
 *
 * Wired in BaseTest via @Listeners.
 */
public class SuiteListener implements ISuiteListener {

    private static final Logger log = LoggerFactory.getLogger(SuiteListener.class);

    @Override
    public void onStart(ISuite suite) {
        log.info("================================================");
        log.info("Suite starting  : {}", suite.getName());
        log.info("Parallel mode   : {}", suite.getParallel());
        log.info("Thread count    : {}", suite.getXmlSuite().getThreadCount());
        log.info("================================================");
    }

    @Override
    public void onFinish(ISuite suite) {
        long passed  = suite.getResults().values().stream()
                .mapToLong(r -> r.getTestContext().getPassedTests().size()).sum();
        long failed  = suite.getResults().values().stream()
                .mapToLong(r -> r.getTestContext().getFailedTests().size()).sum();
        long skipped = suite.getResults().values().stream()
                .mapToLong(r -> r.getTestContext().getSkippedTests().size()).sum();

        log.info("================================================");
        log.info("Suite finished  : {}", suite.getName());
        log.info("Passed : {}  |  Failed : {}  |  Skipped : {}", passed, failed, skipped);
        log.info("================================================");
    }
}
