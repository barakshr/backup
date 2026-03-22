package com.is.deepfake.testng;


import org.openqa.selenium.firefox.FirefoxOptions;

import com.is.infra.selenium.Options;

public class DeepFakeFirefoxOptions implements Options<FirefoxOptions> {

    @Override
    public FirefoxOptions getOptions() {
        return new FirefoxOptions();
    }

}
