package com.is.common.testng.context;

import com.is.common.dto.CompanyDto;

/**
 * Holds company resources provisioned for a single test method.
 * One instance per test (ThreadLocal via CommonContextHolder).
 * Actions use CommonContextUtil.getContext() and set only their field.
 *
 * Retrieve in tests via:
 *   CommonContextHolder.get().getCompany()
 */
public class CommonTestContext {

    private CompanyDto company;

    public CommonTestContext() {
        // empty; actions set their piece via setters
    }

    public CommonTestContext(CompanyDto company) {
        this.company = company;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public void setCompany(CompanyDto company) {
        this.company = company;
    }
}
