package com.is.deepfake.testng.action;

import com.is.deepfake.testng.annotation.DeepfakeAnnotation;
import com.is.deepfake.testng.context.DeepfakeContextHolder;
import com.is.deepfake.testng.context.DeepfakeTestContext;
import com.is.infra.testng.action.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Creates a DFS tenant before the test and deletes it after.
 * Activated when @DeepfakeSetup(createDfsTenant = true) is present on the test method or class.
 *
 * Tenant is stored in DeepfakeContextHolder (ThreadLocal), typed as Object until DfsTenantDto is defined.
 * Retrieve in tests via:
 *   DeepfakeContextHolder.get().getDfsTenant()
 *
 * TODO: inject DFS API client / workflow and implement real create/delete.
 */
public class CreateDfsTenantAction extends AbstractAction {

    private static final Logger log = LoggerFactory.getLogger(CreateDfsTenantAction.class);

    @Override
    public boolean appliesTo(Method method) {
        DeepfakeAnnotation setup = getAnnotation(method, DeepfakeAnnotation.class);
        return setup != null && setup.createDfsTenant();
    }

    @Override
    public void setup(Method method) {
        log.info("CreateDfsTenantAction.setup for test method: {}", method.getName());
        // TODO: create DFS tenant via API and pass it here
        DeepfakeContextHolder.set(new DeepfakeTestContext(null));
    }

    @Override
    public void teardown() {
        log.info("CreateDfsTenantAction.teardown");
        // TODO: delete DFS tenant via API using DeepfakeContextHolder.get().getDfsTenant()
        DeepfakeContextHolder.clear();
    }
}
