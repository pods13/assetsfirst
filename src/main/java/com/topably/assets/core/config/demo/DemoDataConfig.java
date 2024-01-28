package com.topably.assets.core.config.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "app.demo-data", ignoreUnknownFields = false)
@Data
public class DemoDataConfig {

    private String username;

}
