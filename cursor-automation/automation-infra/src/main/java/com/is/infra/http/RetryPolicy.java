package com.is.infra.http;

/**
 * Defines retry behavior for transient HTTP failures (e.g. 429, 503).
 * Stub — to be implemented when retry logic is needed in CI/CD pipelines.
 *
 * Intended usage:
 *   RetryPolicy policy = RetryPolicy.builder()
 *       .maxAttempts(3)
 *       .retryOnStatusCodes(429, 503)
 *       .backoffMillis(1000)
 *       .build();
 *   client.withRetry(policy).get("/endpoint");
 */
public class RetryPolicy {

    private final int maxAttempts;
    private final long backoffMillis;
    private final int[] retryOnStatusCodes;

    private RetryPolicy(Builder builder) {
        this.maxAttempts = builder.maxAttempts;
        this.backoffMillis = builder.backoffMillis;
        this.retryOnStatusCodes = builder.retryOnStatusCodes;
    }

    public int getMaxAttempts() { return maxAttempts; }
    public long getBackoffMillis() { return backoffMillis; }
    public int[] getRetryOnStatusCodes() { return retryOnStatusCodes; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int maxAttempts = 3;
        private long backoffMillis = 1000;
        private int[] retryOnStatusCodes = {429, 503};

        public Builder maxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder backoffMillis(long backoffMillis) {
            this.backoffMillis = backoffMillis;
            return this;
        }

        public Builder retryOnStatusCodes(int... codes) {
            this.retryOnStatusCodes = codes;
            return this;
        }

        public RetryPolicy build() { return new RetryPolicy(this); }
    }
}
