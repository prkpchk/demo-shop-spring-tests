package com.demoshop.qa.ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.demoshop.qa.ui.components.NavbarComponent;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Base page providing common navigation and assertion helpers.
 * All page objects extend this class.
 * Note: relative paths (e.g. "/login") are resolved against Configuration.baseUrl
 *       which is set to app.ui-url in SelenideConfig.
 */
public abstract class BasePage {

    @Step("Navigate to path '{path}'")
    public void navigateTo(String path) {
        open(path);
    }

    @Step("Wait for page heading '{text}'")
    public void waitForHeading(String text) {
        $("h1").shouldHave(text(text));
    }

    /** Returns the navbar component (always present after login). */
    public NavbarComponent navbar() {
        return new NavbarComponent();
    }

    @Step("Assert error message is visible: '{expectedText}'")
    public void assertErrorMessage(String expectedText) {
        $(".error-msg").shouldBe(visible).shouldHave(text(expectedText));
    }

    @Step("Assert success message is visible: '{expectedText}'")
    public void assertSuccessMessage(String expectedText) {
        $(".success-msg").shouldBe(visible).shouldHave(text(expectedText));
    }

    protected SelenideElement loading() {
        return $(".loading");
    }

    @Step("Wait for loading spinner to disappear")
    public void waitForLoadingGone() {
        $(".loading").shouldNotBe(visible);
    }
}
