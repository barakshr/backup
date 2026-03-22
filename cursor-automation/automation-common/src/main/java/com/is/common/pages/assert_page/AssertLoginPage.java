package com.is.common.pages.assert_page;

import com.is.common.pages.CommonLoginPage;
import com.is.infra.selenium.Assert;

/**
 * Assertions for {@link CommonLoginPage} (PatternFly login demo). Constructed
 * via
 * {@link com.is.infra.selenium.BasePage#assertPage(Class)}.
 */
public class AssertLoginPage extends Assert<CommonLoginPage> {

    /** PatternFly v5 login layout — header / title region */

    public AssertLoginPage(CommonLoginPage page) {
        super(page);
    }

    public AssertLoginPage checkLogo() {

        return this;
    }
}
