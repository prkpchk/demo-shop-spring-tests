package com.demoshop.qa.core.config;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.File;

/**
 * Activates when Spring profile 'testcontainers' is set.
 * Starts a fresh PostgreSQL container (datasource auto-configured via @ServiceConnection)
 * and a Selenium Grid container for UI tests.
 */
@TestConfiguration(proxyBeanMethods = false)
@Profile("testcontainers")
public class TestcontainersConfig {

    private static final Logger log = LoggerFactory.getLogger(TestcontainersConfig.class);

    @Bean
    @ServiceConnection                 // Spring Boot 3.1+ auto-configures spring.datasource.* from this
    PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("demoshop")
                .withUsername("demoshop")
                .withPassword("demoshop");
        log.info("Starting Testcontainers PostgreSQL...");
        return container;
    }

    @Bean
    BrowserWebDriverContainer<?> seleniumContainer() {
        File videoDir = new File("build/reports/video");
        videoDir.mkdirs();

        BrowserWebDriverContainer<?> container = new BrowserWebDriverContainer<>()
                .withCapabilities(new ChromeOptions())
                .withRecordingMode(
                        BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING,
                        videoDir
                );
        container.start();

        // Point Selenide at the containerised Selenium Grid
        String remoteUrl = container.getSeleniumAddress().toString();
        log.info("Testcontainers Selenium Grid ready at {}", remoteUrl);
        Configuration.remote = remoteUrl;

        return container;
    }
}
