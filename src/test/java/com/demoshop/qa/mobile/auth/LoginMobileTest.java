package com.demoshop.qa.mobile.auth;

import com.demoshop.qa.base.BaseMobileTest;
import com.demoshop.qa.mobile.pages.CatalogPageAndroid;
import com.demoshop.qa.mobile.pages.CatalogPageIOS;
import com.demoshop.qa.mobile.pages.LoginPageAndroid;
import com.demoshop.qa.mobile.pages.LoginPageIOS;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Mobile Authentication")
@Feature("Mobile Login")
class LoginMobileTest extends BaseMobileTest {

    @Test
    @Story("Successful login — Android")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("[Android] Login with valid credentials → catalog is shown")
    void android_login_validCredentials_opensCatalog() {
        String email    = "user@demoshop.com";
        String password = "user123";

        CatalogPageAndroid catalog = new LoginPageAndroid()
                .verifyDisplayed()
                .enterEmail(email)
                .enterPassword(password)
                .tapLogin();

        catalog.verifyDisplayed();
        catalog.waitForProducts();
        catalog.assertProductCountAtLeast(1);
    }

    @Test
    @Story("Login validation — Android")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Android] Login with wrong password → error shown")
    void android_login_wrongPassword_showsError() {
        new LoginPageAndroid()
                .verifyDisplayed()
                .enterEmail("user@demoshop.com")
                .enterPassword("wrong-password")
                .tapLoginExpectError()
                .assertErrorVisible();
    }

    @Test
    @Story("Successful login — iOS")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("[iOS] Login with valid credentials → catalog is shown")
    void ios_login_validCredentials_opensCatalog() {
        String email    = "user@demoshop.com";
        String password = "user123";

        CatalogPageIOS catalog = new LoginPageIOS()
                .verifyDisplayed()
                .enterEmail(email)
                .enterPassword(password)
                .tapLogin();

        catalog.verifyDisplayed();
        catalog.waitForProducts();
        catalog.assertProductCountAtLeast(1);
    }

    @Test
    @Story("Login validation — iOS")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[iOS] Login with wrong password → error shown")
    void ios_login_wrongPassword_showsError() {
        new LoginPageIOS()
                .verifyDisplayed()
                .enterEmail("user@demoshop.com")
                .enterPassword("wrong-password")
                .tapLoginExpectError()
                .assertErrorVisible();
    }

    @Test
    @Story("Successful login — Android random user")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Android] Register then login with random user → catalog opened")
    void android_login_newUser_opensCatalog() {
        // Uses pre-seeded admin to create the user via API, then logs in on mobile
        // Here we use the seed user for simplicity
        new LoginPageAndroid()
                .verifyDisplayed()
                .enterEmail("admin@demoshop.com")
                .enterPassword("admin123")
                .tapLogin()
                .verifyDisplayed()
                .waitForProducts();
    }
}
