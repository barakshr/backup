package com.is.infra.testng.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares what infrastructure a test method (or all methods in a class) needs
 * before it runs. The SetupOrchestrator reads this annotation and delegates to
 * the appropriate SetupAction implementations.
 *
 * Rules:
 *   - Method-level annotation takes precedence over class-level.
 *   - Tests without this annotation are unaffected — the orchestrator is a no-op.
 *   - Only cross-cutting, product-agnostic fields belong here.
 *     Product-specific fields live in product annotations (e.g. @DeepfakeSetup).
 *
 * Browser/driver is not declared here — it is created lazily when a Page Object
 * is first used and closed after every test by SetupOrchestrator.
 *
 * Example:
 *   @TestSetup(createCompany = true)
 *   @Test
 *   public void shouldDetectDeepfake() { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TestSetup {

    /** Create a test company via AutomationApiClient before the test. Deleted after. */
    boolean createCompany() default false;

    /** Execute database cleanup via AutomationApiClient before the test. */
    boolean cleanDatabase() default false;
}
