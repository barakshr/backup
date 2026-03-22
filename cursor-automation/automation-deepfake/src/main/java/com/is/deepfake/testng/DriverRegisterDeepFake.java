package com.is.deepfake.testng;

import com.is.infra.selenium.BrowserType;
import com.is.infra.selenium.DriverRegister;
import com.is.infra.selenium.Options;

public class DriverRegisterDeepFake implements DriverRegister {

    private BrowserType browserType = BrowserType.CHROME;
    private Options<?> options;
    private static DriverRegisterDeepFake instance;

    private DriverRegisterDeepFake() {
        browserType = BrowserType.CHROME;
        if (browserType == BrowserType.CHROME) {
            options = new DeepFakeChromeOptions();
        }
        if (browserType == BrowserType.FIREFOX) {
            options = new DeepFakeFirefoxOptions();
        }
        if (browserType == BrowserType.EDGE) {
            options = new DeepFakeEdgeOptions();
        }
    }

    public static DriverRegisterDeepFake getInstance() {
        if (instance == null) {
            instance = new DriverRegisterDeepFake();
        }
        return instance;
    }

    @Override
    public BrowserType getBrowserType() {
        return browserType;
    }

    @Override
    public Options<?> getOptions() {
        return options;
    }

}
