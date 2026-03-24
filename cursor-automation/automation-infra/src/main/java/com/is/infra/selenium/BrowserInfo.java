package com.is.infra.selenium;

public interface BrowserInfo {

    public BrowserType getBrowserType();

    public Options<?> getOptions();
}
