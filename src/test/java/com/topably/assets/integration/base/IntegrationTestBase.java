package com.topably.assets.integration.base;

import java.util.Map;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;


public abstract class IntegrationTestBase {

    @ServiceConnection
    private static final MySQLContainer<?> database = new MySQLContainer<>("mysql:8.0.39")
        .withTmpFs(Map.of("/var/lib/mysql", "rw"))
        .withCommand("--character-set-server=utf8 --collation-server=utf8_general_ci")
        .withUrlParam("useSSL", "false");

    static {
        database.start();
    }
}
