package com.topably.assets.integration.base;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.util.Map;

public abstract class IntegrationTestBase {

    private static final MySQLContainer<?> container = new MySQLContainer<>("mysql:5.7.21")
        .withTmpFs(Map.of("/var/lib/mysql", "rw"))
        .withCommand("--character-set-server=utf8 --collation-server=utf8_general_ci");

    @BeforeAll
    static void run() {
        container.start();
    }

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> container.getJdbcUrl() + "?useSSL=false");
    }
}
