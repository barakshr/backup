package com.is.deepfake.testng;

import com.is.deepfake.config.DeepFakeConfig;
import com.is.infra.selenium.Options;
import org.openqa.selenium.edge.EdgeOptions;

public class DeepFakeEdgeOptions implements Options<EdgeOptions> {

    @Override
    public EdgeOptions getOptions() {
        EdgeOptions opts = new EdgeOptions();

        // config-driven
        if (DeepFakeConfig.get().isBrowserHeadless()) {
            opts.addArguments("--headless");
        }

        return opts;
    }
}
