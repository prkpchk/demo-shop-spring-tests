package com.demoshop.qa.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * iOS-specific login page using XCUITest / accessibility-id and class-chain locators.
 */
public class LoginPageIOS extends AbstractMobilePage {

    // Accessibility-id selectors (iOS)
    private static final String EMAIL_FIELD    = "email_input";
    private static final String PASSWORD_FIELD = "password_input";
    private static final String LOGIN_BUTTON   = "login_button";
    private static final String ERROR_LABEL    = "error_label";
    private static final String REGISTER_LINK  = "register_link";

    @Step("Enter email '{email}'")
    public LoginPageIOS enterEmail(String email) {
        typeByAccessibilityId(EMAIL_FIELD, email);
        return this;
    }

    @Step("Enter password")
    public LoginPageIOS enterPassword(String password) {
        typeByAccessibilityId(PASSWORD_FIELD, password);
        return this;
    }

    @Step("Tap Login button")
    public CatalogPageIOS tapLogin() {
        tapByAccessibilityId(LOGIN_BUTTON);
        return new CatalogPageIOS();
    }

    @Step("Tap Login button and expect error")
    public LoginPageIOS tapLoginExpectError() {
        tapByAccessibilityId(LOGIN_BUTTON);
        $(AppiumBy.accessibilityId(ERROR_LABEL)).shouldBe(visible);
        return this;
    }

    @Step("Assert error message visible")
    public LoginPageIOS assertErrorVisible() {
        $(AppiumBy.accessibilityId(ERROR_LABEL)).shouldBe(visible);
        return this;
    }

    @Step("Tap 'Register' link")
    public void tapRegister() {
        tapByAccessibilityId(REGISTER_LINK);
    }

    @Step("Verify login screen is shown via class chain")
    public LoginPageIOS verifyDisplayed() {
        // Class chain: **/XCUIElementTypeButton[`name == 'login_button'`]
        $(AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`name == 'login_button'`]"))
                .shouldBe(visible);
        return this;
    }
}
