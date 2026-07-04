package com.demoshop.qa.core.config;

import com.codeborne.selenide.Configuration;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "selenide")
public class SelenideConfig {

    private String browser = "chrome";
    private boolean headless = false;
    private long timeout = 10000;
    private long pageLoadTimeout = 30000;
    private String reportsFolder = "build/reports/selenide";
    private String videoFolder = "build/reports/video";

    @Autowired
    private AppConfig appConfig;

    @PostConstruct
    public void apply() {
        Configuration.browser = browser;
        Configuration.headless = headless;
        Configuration.timeout = timeout;
        Configuration.pageLoadTimeout = pageLoadTimeout;
        Configuration.reportsFolder = reportsFolder;
        Configuration.screenshots = true;
        Configuration.savePageSource = false;
        Configuration.browserSize = "1920x1080";
        // baseUrl lets page objects use relative paths like open("/login")
        Configuration.baseUrl = appConfig.getUiUrl();
    }
}
