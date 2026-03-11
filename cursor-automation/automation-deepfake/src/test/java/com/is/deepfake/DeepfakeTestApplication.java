package com.is.deepfake;

import com.is.deepfake.config.DemoToolProperties;
import com.is.infra.http.HttpClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Spring Boot entry point for the Deepfake test module.
 *
 * Registers all @ConfigurationProperties beans explicitly:
 *   - HttpClientProperties  (infra — global HTTP config)
 *   - DemoToolProperties    (deepfake — demo tool credentials + URL)
 *
 * Referenced by @SpringBootTest(classes = DeepfakeTestApplication.class)
 * in DeepfakeBaseTest. Spring creates the application context once per suite
 * and reuses it across all test classes.
 */
@SpringBootApplication(scanBasePackages = {"com.is.infra", "com.is.deepfake"})
@EnableConfigurationProperties({
        HttpClientProperties.class,
        DemoToolProperties.class
})
public class DeepfakeTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeepfakeTestApplication.class, args);
    }
}
