package com.demoshop.qa.base;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.demoshop.qa.core.config.AppConfig;
import com.demoshop.qa.core.config.RetrofitConfig;
import com.demoshop.qa.dataproviders.FakerUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

/**
 * Base class for all Selenide UI tests.
 * Registers the AllureSelenide listener (screenshots + step logging),
 * opens a fresh browser before each test, and closes it after.
 * Screenshots are attached to Allure after every test (pass or fail).
 */
public abstract class BaseUiTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseUiTest.class);

    @Autowired
    protected AppConfig appConfig;

    @Autowired
    protected RetrofitConfig retrofitConfig;

    @Autowired
    protected FakerUtils fakerUtils;

    @BeforeAll
    static void registerAllureListener() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
    }

    @BeforeEach
    void openBrowser(TestInfo testInfo) {
        log.info("Starting UI test: {}", testInfo.getDisplayName());
        Selenide.open(appConfig.getUiUrl());
    }

    @AfterEach
    void closeBrowser(TestInfo testInfo) {
        attachScreenshot();
        log.info("Finished UI test: {}", testInfo.getDisplayName());
        Selenide.closeWebDriver();
    }

    private void attachScreenshot() {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            return;
        }
        try {
            byte[] bytes = ((TakesScreenshot) WebDriverRunner.getWebDriver())
                    .getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot (end of test)", "image/png",
                    new ByteArrayInputStream(bytes), ".png");
        } catch (Exception e) {
            log.warn("Could not capture screenshot for Allure report", e);
        }
    }
}
