package com.is.common.workflows;

import com.is.common.dto.CompanyDto;

/**
 * Reusable multi-step workflow: create a company and configure its integration.
 * Stub — to be implemented when company lifecycle tests are needed.
 *
 * Used by test @BeforeClass or @BeforeMethod setup across multiple product modules.
 *
 * Example usage:
 *   CompanyDto company = CompanySetupWorkflow.create()
 *       .withName("test-company-" + randomSuffix())
 *       .withO365Domain("wj1b1.onmicrosoft.com")
 *       .withDeepfakeEnabled(true)
 *       .build();
 */
public class CompanySetupWorkflow {

    public CompanySetupWorkflow() {
        throw new UnsupportedOperationException("CompanySetupWorkflow not yet implemented");
    }

    /**
     * Creates a company using the AutomationApiClient.
     * TODO: implement
     */
    public CompanyDto create() {
        throw new UnsupportedOperationException("CompanySetupWorkflow.create() not yet implemented");
    }

    /**
     * Deletes the company and releases any associated resources.
     * Called in test cleanup.
     * TODO: implement
     */
    public void delete(CompanyDto company) {
        throw new UnsupportedOperationException("CompanySetupWorkflow.delete() not yet implemented");
    }
}
