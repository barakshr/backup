package com.is.common.testng.setup;

import com.is.common.dto.CompanyDto;
import com.is.common.testng.CommonContextHolder;
import com.is.common.testng.CommonTestContext;
import com.is.common.workflows.CompanySetupWorkflow;
import com.is.infra.testng.annotation.TestSetup;
import com.is.infra.testng.setup.AbstractSetupAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Creates a test company before the test and deletes it after.
 * Activated when @TestSetup(createCompany=true) is present on the test method or class.
 *
 * Company is stored in CommonContextHolder (ThreadLocal), typed as CompanyDto.
 * Retrieve in tests via:
 *   CompanyDto company = CommonContextHolder.get().getCompany();
 *
 * Registration:
 *   Product base tests register this action in @BeforeSuite:
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
    public void setup(Method method) {
        log.info("Creating company for test: {}", method.getName());
        CompanyDto company = companyWorkflow.create().build();
        CommonContextHolder.set(new CommonTestContext(company));
    }

    @Override
    public void teardown() {
        CommonTestContext ctx = CommonContextHolder.get();
        if (ctx != null && ctx.getCompany() != null) {
            log.info("Deleting company: {}", ctx.getCompany().getCompanyId());
            companyWorkflow.delete(ctx.getCompany());
            CommonContextHolder.clear();
        }
    }
}
