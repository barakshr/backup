package com.is.deepfake.selenium.pages;

import org.openqa.selenium.WebDriver;

import com.is.common.pages.LoginPage;
import com.is.infra.selenium.DriverHolder;

public class NextPage extends LoginPage {

    public NextPage() {
        super();
    }       

    public NextPage(WebDriver driver) {
        super(driver);
        DriverHolder.setDriver(driver);
    }


}
