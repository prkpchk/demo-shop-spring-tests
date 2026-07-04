package com.demoshop.qa.ui.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage extends BasePage {

    private static final String PATH = "/login";

    @Step("Open login page")
    public LoginPage open() {
        navigateTo(PATH);
        $(".auth-card h1").shouldHave(text("Login"));
        return this;
    }

    @Step("Enter email '{email}'")
    public LoginPage enterEmail(String email) {
        $(".auth-card input[type='email']").setValue(email);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        $(".auth-card input[type='password']").setValue(password);
        return this;
    }

    @Step("Click Login button")
    public CatalogPage clickLogin() {
        $(".auth-card .btn-primary").click();
        return new CatalogPage();
    }

    @Step("Submit login form and expect failure")
    public LoginPage clickLoginExpectFailure() {
        $(".auth-card .btn-primary").click();
        $(".error-msg").shouldBe(visible);
        return this;
    }

    @Step("Login with email '{email}'")
    public CatalogPage loginAs(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        return clickLogin();
    }

    @Step("Click 'Register' link")
    public RegisterPage goToRegister() {
        $(".auth-link a[href='/register']").click();
        return new RegisterPage();
    }

    @Step("Assert error message visible")
    public LoginPage assertError(String message) {
        $(".error-msg").shouldBe(visible).shouldHave(text(message));
        return this;
    }
}
