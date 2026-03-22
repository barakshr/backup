package com.is.deepfake.selenium.pages;

import org.openqa.selenium.WebDriver;

import com.is.common.pages.CommonLoginPage;
import com.is.infra.selenium.DriverHolder;

public class NextPage extends CommonLoginPage {

    public NextPage() {
        super();
    }

    public NextPage(WebDriver driver) {
        super(driver);
        DriverHolder.setDriver(driver);
    }

    public NextPage clickonMe() {
        return this;
    }

    public CommonLoginPage returnToLoginPage() {
        return new CommonLoginPage();
    }
}
