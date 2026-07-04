package com.demoshop.qa.ui.components;

import com.demoshop.qa.ui.pages.CartPage;
import com.demoshop.qa.ui.pages.LoginPage;
import com.demoshop.qa.ui.pages.OrdersPage;
import com.demoshop.qa.ui.pages.ProfilePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page-object fragment for the global navigation bar.
 */
public class NavbarComponent {

    @Step("Click 'Cart' in navbar")
    public CartPage goToCart() {
        $(".navbar-links a[href='/cart']").click();
        return new CartPage();
    }

    @Step("Click 'Orders' in navbar")
    public OrdersPage goToOrders() {
        $(".navbar-links a[href='/orders']").click();
        return new OrdersPage();
    }

    @Step("Click profile link in navbar")
    public ProfilePage goToProfile() {
        $(".navbar-links a[href='/profile']").click();
        return new ProfilePage();
    }

    @Step("Click 'Logout' in navbar")
    public LoginPage logout() {
        $(".btn-link").shouldHave(text("Logout")).click();
        return new LoginPage();
    }

    @Step("Assert user name '{name}' is shown in navbar")
    public void assertUserName(String name) {
        $(".navbar-links").shouldHave(text(name));
    }

    @Step("Assert user is not logged in (Login link is visible)")
    public void assertNotLoggedIn() {
        $(".navbar-links a[href='/login']").shouldBe(visible);
    }

    @Step("Assert user is logged in (Logout button is visible)")
    public void assertLoggedIn() {
        $(".btn-link").shouldBe(visible).shouldHave(text("Logout"));
    }
}
