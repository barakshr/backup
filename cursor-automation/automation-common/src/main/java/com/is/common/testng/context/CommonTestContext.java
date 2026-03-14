package com.is.common.testng.context;

import com.is.common.dto.CompanyDto;

/**
 * Holds company resources provisioned for a single test method.
 * Stored in CommonContextHolder (ThreadLocal) — one instance per thread, parallel-safe.
 * Lifecycle managed by CreateCompanyAction.
 *
 * Retrieve in tests via:
 *   CompanyDto company = CommonContextHolder.get().getCompany();
 */
public class CommonTestContext {

    private final CompanyDto company;

    public CommonTestContext(CompanyDto company) {
        this.company = company;
    }

    public CompanyDto getCompany() {
        return company;
    }
}
