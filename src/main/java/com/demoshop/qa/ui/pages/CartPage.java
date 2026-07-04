package com.demoshop.qa.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CartPage extends BasePage {

    private static final String PATH = "/cart";

    @Step("Open cart page")
    public CartPage open() {
        navigateTo(PATH);
        $(".cart-page h1").shouldHave(text("Your Cart"));
        return this;
    }

    @Step("Wait for cart to load")
    public CartPage waitForLoad() {
        $(".cart-page").shouldBe(visible);
        return this;
    }

    public ElementsCollection cartItems() {
        return $$(".cart-item");
    }

    @Step("Click 'Remove' for item #{index}")
    public CartPage removeItem(int index) {
        $$(".cart-item .btn-danger").get(index).click();
        return this;
    }

    @Step("Set quantity for item #{index} to {quantity}")
    public CartPage setItemQuantity(int index, int quantity) {
        $$(".quantity-input").get(index).setValue(String.valueOf(quantity));
        return this;
    }

    @Step("Click 'Place Order'")
    public OrdersPage placeOrder() {
        $(".cart-summary .btn-primary").shouldHave(text("Place Order")).click();
        return new OrdersPage();
    }

    @Step("Get cart total text")
    public String getCartTotal() {
        return $(".cart-total").text();
    }

    @Step("Assert cart is empty")
    public CartPage assertEmpty() {
        $(".empty-state").shouldBe(visible).shouldHave(text("Your cart is empty"));
        return this;
    }

    @Step("Assert cart has {count} items")
    public CartPage assertItemCount(int count) {
        $$(".cart-item").shouldHave(size(count));
        return this;
    }

    @Step("Assert cart total contains '{totalText}'")
    public CartPage assertTotalContains(String totalText) {
        $(".cart-total").shouldHave(text(totalText));
        return this;
    }
}
