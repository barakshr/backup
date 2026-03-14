package com.is.common.workflows;

import com.is.common.clients.AutomationApiClient;
import com.is.common.dto.CompanyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fluent builder for test company lifecycle.
 *
 * Used by CreateCompanyAction and directly in @BeforeClass for class-scoped companies.
 *
 * Usage:
 *   CompanyDto company = companyWorkflow.create()
 *       .withName("test-" + UUID.randomUUID())
 *       .withO365Domain("wj1b1.onmicrosoft.com")
 *       .withDeepfakeEnabled(true)
 *       .build();
 *   ...
 *   companyWorkflow.delete(company);
 *
 * Note: build() and delete() throw UnsupportedOperationException until
 * AutomationApiClient is implemented. See issuesBacklog.md.
 */
public class CompanySetupWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CompanySetupWorkflow.class);

    private final AutomationApiClient automationApiClient = new AutomationApiClient();


    public Builder create() {
        return new Builder(automationApiClient);
    }

    public void delete(CompanyDto company) {
        if (company == null) return;
        log.info("Deleting company: {} ({})", company.getCompanyName(), company.getCompanyId());
        // TODO: implement when AutomationApiClient is ready
        throw new UnsupportedOperationException(
                "CompanySetupWorkflow.delete() not yet implemented — AutomationApiClient stub");
    }

    public static class Builder {

        private final AutomationApiClient automationApiClient;
        private String  name;
        private String  o365Domain;
        private boolean deepfakeEnabled = false;
        private String  planType        = "CORE_PLAN";

        Builder(AutomationApiClient automationApiClient) {
            this.automationApiClient = automationApiClient;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withO365Domain(String o365Domain) {
            this.o365Domain = o365Domain;
            return this;
        }

        public Builder withDeepfakeEnabled(boolean enabled) {
            this.deepfakeEnabled = enabled;
            return this;
        }

        public Builder withPlanType(String planType) {
            this.planType = planType;
            return this;
        }

        public CompanyDto build() {
            log.info("Creating company: name={}, o365Domain={}, deepfake={}, plan={}",
                    name, o365Domain, deepfakeEnabled, planType);
            // TODO: implement when AutomationApiClient is ready
            throw new UnsupportedOperationException(
                    "CompanySetupWorkflow.build() not yet implemented — AutomationApiClient stub");
        }
    }
}
