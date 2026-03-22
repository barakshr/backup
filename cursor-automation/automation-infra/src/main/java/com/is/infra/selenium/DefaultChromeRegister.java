package com.is.infra.selenium;

public class DefaultChromeRegister implements DriverRegister {

    @Override
    public BrowserType getBrowserType() {
        return BrowserType.CHROME;
    }

    @Override
    public Options<?> getOptions() {
        return new DefaultChromeOptions();
    }
}