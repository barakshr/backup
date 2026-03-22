package com.is.deepfake.testng;

import com.is.infra.selenium.Options;

public class DeepFakeEdgeOptions implements Options<DeepFakeEdgeOptions> {

    @Override
    public DeepFakeEdgeOptions getOptions() {
       return new DeepFakeEdgeOptions();
    }

}
