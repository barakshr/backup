package com.is.deepfake.testng;

import com.is.deepfake.config.DeepFakeConfig;
import com.is.infra.selenium.BrowserInfo;
import com.is.infra.selenium.BrowserType;
import com.is.infra.selenium.Options;

/**
 * Deepfake implementation of {@link BrowserInfo}.
 * <p>
 * Reads browser type from {@link DeepFakeConfig} and selects the matching
 * deepfake-specific options class. The options class itself is responsible for
 * applying both hardcoded and config-driven browser arguments (e.g. headless).
 * <p>
 * Eager singleton — initialized at class-load time, which happens when
 * {@code DeepfakeBaseTest.@BeforeSuite} first references this class.
 * At that point {@link DeepFakeConfig} is already initialized and validated.
 */
public class BrowserInfoDeepFake implements BrowserInfo {

    private static final BrowserInfoDeepFake INSTANCE = new BrowserInfoDeepFake();

    private final BrowserType browserType;
    private final Options<?> options;

    private BrowserInfoDeepFake() {
        DeepFakeConfig cfg = DeepFakeConfig.get();
        this.browserType = cfg.getBrowserType();
        this.options = switch (browserType) {
            case CHROME -> new DeepFakeChromeOptions();
            case FIREFOX -> new DeepFakeFirefoxOptions();
            case EDGE -> new DeepFakeEdgeOptions();
        };
    }

    public static BrowserInfoDeepFake get() {
        return INSTANCE;
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
