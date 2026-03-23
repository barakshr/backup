package com.is.deepfake.testng;

import com.is.deepfake.config.DeepFakeConfig;
import com.is.infra.selenium.Options;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DeepFakeFirefoxOptions implements Options<FirefoxOptions> {

    @Override
    public FirefoxOptions getOptions() {
        FirefoxOptions opts = new FirefoxOptions();

        // config-driven
        if (DeepFakeConfig.get().isBrowserHeadless()) {
            opts.addArguments("-headless");
        }

        return opts;
    }
}
