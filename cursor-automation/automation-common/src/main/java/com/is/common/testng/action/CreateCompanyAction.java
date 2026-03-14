package com.is.common.testng.action;

import com.is.common.dto.CompanyDto;
import com.is.common.testng.annotation.CommonAnnotation;
import com.is.common.testng.context.CommonContextHolder;
import com.is.common.testng.context.CommonTestContext;
import com.is.common.workflows.CompanySetupWorkflow;
import com.is.infra.testng.action.AbstractAction;
import com.is.infra.testng.annotation.InfraAnnotation;

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
public class CreateCompanyAction extends AbstractAction {

    private static final Logger log = LoggerFactory.getLogger(CreateCompanyAction.class);

    private final CompanySetupWorkflow companyWorkflow = new CompanySetupWorkflow();

    @Override
    public boolean appliesTo(Method method) {
        CommonAnnotation setup = getAnnotation(method, CommonAnnotation.class);
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
