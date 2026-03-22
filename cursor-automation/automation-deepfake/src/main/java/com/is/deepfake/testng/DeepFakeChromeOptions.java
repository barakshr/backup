package com.is.deepfake.testng;

import com.is.infra.selenium.Options;
import org.openqa.selenium.chrome.ChromeOptions;

public class DeepFakeChromeOptions implements Options<ChromeOptions> {

    @Override
    public ChromeOptions getOptions() {
        org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        return options;
    }

}
    