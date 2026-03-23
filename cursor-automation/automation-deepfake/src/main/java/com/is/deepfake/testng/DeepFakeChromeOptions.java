package com.is.deepfake.testng;

import com.is.deepfake.config.DeepFakeConfig;
import com.is.infra.selenium.Options;
import org.openqa.selenium.chrome.ChromeOptions;

public class DeepFakeChromeOptions implements Options<ChromeOptions> {

    @Override
    public ChromeOptions getOptions() {
        ChromeOptions opts = new ChromeOptions();

        // hardcoded — always applied
        opts.addArguments("--no-sandbox");
        opts.addArguments("--disable-dev-shm-usage");
        opts.addArguments("--disable-gpu");
        opts.addArguments("--window-size=1920,1080");
        opts.addArguments("--disable-extensions");

        // config-driven
        if (DeepFakeConfig.get().isBrowserHeadless()) {
            opts.addArguments("--headless=new");
        }

        return opts;
    }
}
