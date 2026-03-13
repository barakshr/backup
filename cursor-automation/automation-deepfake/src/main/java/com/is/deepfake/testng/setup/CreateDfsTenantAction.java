package com.is.deepfake.testng.setup;

import com.is.deepfake.testng.annotation.DeepfakeSetup;
import com.is.infra.testng.TestContext;
import com.is.infra.testng.setup.AbstractSetupAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Creates a DFS tenant before the test and deletes it after.
 * Activated when @DeepfakeSetup(createDfsTenant = true) is present on the test method or class.
 *
 * When implemented, the tenant is stored in TestContext under {@link #EXTRA_DFS_TENANT}.
 * Tests retrieve it via: TestContextHolder.get().getExtra(CreateDfsTenantAction.EXTRA_DFS_TENANT)
 *
 * TODO: inject DFS API client / workflow and implement real create/delete.
 */
public class CreateDfsTenantAction extends AbstractSetupAction {

    /** Key used to store the DFS tenant in TestContext.extras. */
    public static final String EXTRA_DFS_TENANT = "dfsTenant";

    private static final Logger log = LoggerFactory.getLogger(CreateDfsTenantAction.class);

    @Override
    public boolean appliesTo(Method method) {
        DeepfakeSetup setup = getAnnotation(method, DeepfakeSetup.class);
        return setup != null && setup.createDfsTenant();
    }

    @Override
    public void setup(TestContext.Builder builder, Method method) {
        log.info("CreateDfsTenantAction.setup for test method: {}", method.getName());
        // TODO: create DFS tenant via API, then builder.withExtra(EXTRA_DFS_TENANT, tenant);
    }

    @Override
    public void teardown(TestContext ctx) {
        log.info("CreateDfsTenantAction.teardown");
        // TODO: delete DFS tenant when API client is available
    }
}
