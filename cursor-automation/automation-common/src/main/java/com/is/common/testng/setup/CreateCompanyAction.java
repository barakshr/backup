package com.is.common.testng.setup;

import com.is.common.dto.CompanyDto;
import com.is.common.workflows.CompanySetupWorkflow;
import com.is.infra.testng.TestContext;
import com.is.infra.testng.annotation.TestSetup;
import com.is.infra.testng.setup.AbstractSetupAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Creates a test company before the test and deletes it after.
 * Activated when @TestSetup(createCompany=true) is present on the test method or class.
 *
 * Company is stored in TestContext as Object (infra is product-agnostic).
 * Retrieve it in tests via:
 *   CompanyDto company = (CompanyDto) TestContextHolder.get().getCompany();
 *
 * Registration:
 *   Each product base test registers this action in @BeforeSuite alongside
 *   its own AutomationApiClient / CompanySetupWorkflow dependency:
 *
 *   @Autowired private CompanySetupWorkflow companyWorkflow;
 *
 *   @BeforeSuite(alwaysRun = true)
 *   public void registerCommonActions() {
 *       SetupActionRegistry.register(new CreateCompanyAction(companyWorkflow));
 *   }
 *
 * Note: build() and delete() are stubs until AutomationApiClient is implemented.
 * See issuesBacklog.md for details.
 */
public class CreateCompanyAction extends AbstractSetupAction {

    private static final Logger log = LoggerFactory.getLogger(CreateCompanyAction.class);

    private final CompanySetupWorkflow companyWorkflow;

    public CreateCompanyAction(CompanySetupWorkflow companyWorkflow) {
        this.companyWorkflow = companyWorkflow;
    }

    @Override
    public boolean appliesTo(Method method) {
        TestSetup setup = getAnnotation(method, TestSetup.class);
        return setup != null && setup.createCompany();
    }

    @Override
    public void setup(TestContext.Builder builder, Method method) {
        log.info("Creating company for test: {}", method.getName());
        CompanyDto company = companyWorkflow.create().build();
        builder.withCompany(company);
    }

    @Override
    public void teardown(TestContext ctx) {
        if (ctx.getCompany() instanceof CompanyDto company) {
            log.info("Deleting company: {}", company.getCompanyId());
            companyWorkflow.delete(company);
        }
    }
}
