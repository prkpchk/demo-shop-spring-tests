package com.demoshop.qa.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Android-specific login page using UIAutomator2 / resource-id locators.
 */
public class LoginPageAndroid extends AbstractMobilePage {

    // Resource-id selectors (Android)
    private static final String EMAIL_FIELD    = "com.demoshop:id/et_email";
    private static final String PASSWORD_FIELD = "com.demoshop:id/et_password";
    private static final String LOGIN_BUTTON   = "com.demoshop:id/btn_login";
    private static final String ERROR_LABEL    = "com.demoshop:id/tv_error";
    private static final String REGISTER_LINK  = "com.demoshop:id/tv_register";

    @Step("Enter email '{email}'")
    public LoginPageAndroid enterEmail(String email) {
        typeById(EMAIL_FIELD, email);
        return this;
    }

    @Step("Enter password")
    public LoginPageAndroid enterPassword(String password) {
        typeById(PASSWORD_FIELD, password);
        return this;
    }

    @Step("Tap Login button")
    public CatalogPageAndroid tapLogin() {
        tapById(LOGIN_BUTTON);
        return new CatalogPageAndroid();
    }

    @Step("Tap Login button and expect error")
    public LoginPageAndroid tapLoginExpectError() {
        tapById(LOGIN_BUTTON);
        $(AppiumBy.id(ERROR_LABEL)).shouldBe(visible);
        return this;
    }

    @Step("Assert error message visible")
    public LoginPageAndroid assertErrorVisible() {
        $(AppiumBy.id(ERROR_LABEL)).shouldBe(visible);
        return this;
    }

    @Step("Tap 'Register' link")
    public void tapRegister() {
        tapById(REGISTER_LINK);
    }

    @Step("Verify login screen is shown")
    public LoginPageAndroid verifyDisplayed() {
        $(AppiumBy.id(LOGIN_BUTTON)).shouldBe(visible);
        return this;
    }
}
