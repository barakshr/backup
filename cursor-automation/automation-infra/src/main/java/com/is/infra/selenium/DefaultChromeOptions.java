package com.is.infra.selenium;

import org.openqa.selenium.chrome.ChromeOptions;

public class DefaultChromeOptions implements Options<ChromeOptions> {

    @Override
    public ChromeOptions getOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        return options;
    }
}
