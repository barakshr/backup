package com.is.deepfake.testng.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Deepfake-specific test setup declaration.
 *
 * Used alongside @TestSetup for fields that are specific to the Deepfake domain.
 * Kept separate from @TestSetup to preserve the infra layer's product-agnosticism.
 *
 * Each field corresponds to a SetupAction implementation in
 * automation-deepfake that reads this annotation.
 *
 * Example:
 *   @TestSetup(createCompany = true)
 *   @DeepfakeSetup(createDfsTenant = true)
 *   @Test
 *   public void shouldDetectDeepfakeInMeeting() {
 *       CompanyDto company = CommonContextHolder.get().getCompany();
 *       Object     tenant  = DeepfakeContextHolder.get().getDfsTenant();
 *   }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DeepfakeSetup {

    /** Create a DFS tenant and associate it with the test company before the test. Deleted after. */
    boolean createDfsTenant() default false;

    /** Join a Teams meeting as a deepfake participant before the test. */
    boolean joinTeamsMeeting() default false;

    /** Scenario name passed to the meeting setup (e.g. "with-deepfake", "no-deepfake"). */
    String meetingScenario() default "";
}
