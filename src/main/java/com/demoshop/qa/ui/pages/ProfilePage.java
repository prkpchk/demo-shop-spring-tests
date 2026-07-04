package com.demoshop.qa.ui.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage extends BasePage {

    private static final String PATH = "/profile";

    @Step("Open profile page")
    public ProfilePage open() {
        navigateTo(PATH);
        $(".profile-page h1").shouldHave(text("Profile"));
        return this;
    }

    @Step("Wait for profile to load")
    public ProfilePage waitForLoad() {
        $(".profile-card").shouldBe(visible);
        return this;
    }

    @Step("Enter top-up amount '{amount}'")
    public ProfilePage enterTopUpAmount(String amount) {
        $(".amount-input").setValue(amount);
        return this;
    }

    @Step("Click 'Top Up'")
    public ProfilePage clickTopUp() {
        $(".topup-form .btn-primary").shouldHave(text("Top Up")).click();
        return this;
    }

    @Step("Get displayed balance text")
    public String getBalanceText() {
        return $(".balance-amount").text();
    }

    @Step("Get displayed user name")
    public String getUserName() {
        return $(".profile-field:first-child p").text();
    }

    @Step("Get displayed email")
    public String getEmail() {
        return $$(".profile-field p").get(1).text();
    }

    @Step("Assert name is '{name}'")
    public ProfilePage assertName(String name) {
        $$(".profile-field p").first().shouldHave(text(name));
        return this;
    }

    @Step("Assert email is '{email}'")
    public ProfilePage assertEmail(String email) {
        $$(".profile-field p").get(1).shouldHave(text(email));
        return this;
    }

    @Step("Assert role is '{role}'")
    public ProfilePage assertRole(String role) {
        $$(".profile-field p").get(2).shouldHave(text(role));
        return this;
    }

    @Step("Assert balance text contains '{amount}'")
    public ProfilePage assertBalanceContains(String amount) {
        $(".balance-amount").shouldHave(text(amount));
        return this;
    }

    @Step("Assert top-up success message is shown")
    public ProfilePage assertTopUpSuccess() {
        $(".success-msg").shouldBe(visible);
        return this;
    }
}
