package com.is.common.pages.assert_page;

import com.is.common.pages.LoginPage;
import com.is.infra.selenium.Assert;


/**
 * Assertions for {@link LoginPage} (PatternFly login demo). Constructed via
 * {@link com.is.infra.selenium.BasePage#assertPage(Class)}.
 */
public class AssertLoginPage extends Assert<LoginPage> {

    /** PatternFly v5 login layout — header / title region */
   
    public AssertLoginPage(LoginPage page) {
        super(page);
    }

    public AssertLoginPage checkLogo() {

        return this;
    }
}
