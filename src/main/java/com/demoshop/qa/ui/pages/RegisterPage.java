package com.demoshop.qa.ui.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage {

    private static final String PATH = "/register";

    @Step("Open register page")
    public RegisterPage open() {
        navigateTo(PATH);
        $(".auth-card h1").shouldHave(text("Register"));
        return this;
    }

    @Step("Enter name '{name}'")
    public RegisterPage enterName(String name) {
        $(".auth-card input[type='text']").setValue(name);
        return this;
    }

    @Step("Enter email '{email}'")
    public RegisterPage enterEmail(String email) {
        $(".auth-card input[type='email']").setValue(email);
        return this;
    }

    @Step("Enter password")
    public RegisterPage enterPassword(String password) {
        $(".auth-card input[type='password']").setValue(password);
        return this;
    }

    @Step("Click Register button")
    public CatalogPage clickRegister() {
        $(".auth-card .btn-primary").click();
        return new CatalogPage();
    }

    @Step("Click Register button and expect failure")
    public RegisterPage clickRegisterExpectFailure() {
        $(".auth-card .btn-primary").click();
        // HTML5 validation (e.g. minLength on password) blocks submission without
        // rendering .error-msg, so the reliable signal is staying on the form
        $(".auth-card h1").shouldHave(text("Register"));
        return this;
    }

    @Step("Register as name='{name}', email='{email}'")
    public CatalogPage registerAs(String name, String email, String password) {
        enterName(name);
        enterEmail(email);
        enterPassword(password);
        return clickRegister();
    }

    @Step("Click 'Login' link")
    public LoginPage goToLogin() {
        $(".auth-link a[href='/login']").click();
        return new LoginPage();
    }

    @Step("Assert error message visible")
    public RegisterPage assertError(String message) {
        $(".error-msg").shouldBe(visible).shouldHave(text(message));
        return this;
    }
}
