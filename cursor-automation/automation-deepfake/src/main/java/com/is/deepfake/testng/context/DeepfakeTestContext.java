package com.is.deepfake.testng.context;

import com.is.deepfake.product.Meeting;

/**
 * Holds deepfake-specific test resources for a single test method.
 * One instance per test (ThreadLocal via DeepfakeContextHolder).
 * Actions use DeepfakeContextUtil.getContext() and set only their field.
 *
 * dfsTenant is typed as Object until DfsTenantDto is defined.
 *
 * Retrieve in tests via:
 *   DeepfakeContextHolder.get().getDfsTenant()
 *   DeepfakeContextHolder.get().getMeeting()
 */
public class DeepfakeTestContext {

    private Object dfsTenant;
    private Meeting meeting;

    public DeepfakeTestContext() {
        // empty; actions set their piece via setters
    }

    public Object getDfsTenant() {
        return dfsTenant;
    }

    public void setDfsTenant(Object dfsTenant) {
        this.dfsTenant = dfsTenant;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }
}
