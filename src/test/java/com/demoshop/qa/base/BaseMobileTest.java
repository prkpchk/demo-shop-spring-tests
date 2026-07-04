package com.demoshop.qa.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.demoshop.qa.TestApplication;
import com.demoshop.qa.core.junit.extensions.RetryExtension;
import com.demoshop.qa.core.config.MobileDriverConfig;
import com.demoshop.qa.dataproviders.FakerUtils;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Base class for all mobile tests.
 * Activates the 'mobile' Spring profile which loads application-mobile.yml
 * and registers the MobileDriverConfig as the Selenide WebDriverProvider.
 */
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(RetryExtension.class)
@Profile("mobile")
public abstract class BaseMobileTest {

    private static final Logger log = LoggerFactory.getLogger(BaseMobileTest.class);

    @Autowired
    protected MobileDriverConfig mobileDriverConfig;

    @Autowired
    protected FakerUtils fakerUtils;

    @BeforeAll
    static void registerAllureListener() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
    }

    @BeforeEach
    void setUpDriver(TestInfo testInfo) {
        log.info("Starting mobile test: {} [{}]",
                testInfo.getDisplayName(),
                mobileDriverConfig.isAndroid() ? "Android" : "iOS");
        Configuration.browser = mobileDriverConfig.getClass().getName();
        Configuration.browserSize = null; // Not applicable for mobile
    }

    @AfterEach
    void tearDownDriver(TestInfo testInfo) {
        log.info("Finished mobile test: {}", testInfo.getDisplayName());
        Selenide.closeWebDriver();
    }
}
