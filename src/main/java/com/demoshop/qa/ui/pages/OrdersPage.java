package com.demoshop.qa.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class OrdersPage extends BasePage {

    private static final String PATH = "/orders";

    @Step("Open orders page")
    public OrdersPage open() {
        navigateTo(PATH);
        $(".orders-page h1").shouldHave(text("My Orders"));
        return this;
    }

    @Step("Wait for orders to load")
    public OrdersPage waitForLoad() {
        $(".orders-page").shouldBe(visible);
        $(".loading").shouldNotBe(visible);
        return this;
    }

    public ElementsCollection orderCards() {
        return $$(".order-card");
    }

    @Step("Click 'Pay' for the first PENDING order")
    public PaymentModalPage clickPayForFirstPendingOrder() {
        $$(".order-footer .btn-primary").findBy(text("Pay")).click();
        return new PaymentModalPage();
    }

    @Step("Assert first order has status '{status}'")
    public OrdersPage assertFirstOrderStatus(String status) {
        $$(".order-status").first().shouldHave(text(status));
        return this;
    }

    @Step("Assert at least {count} orders are shown")
    public OrdersPage assertOrderCountAtLeast(int count) {
        $$(".order-card").shouldHave(sizeGreaterThanOrEqual(count));
        return this;
    }

    @Step("Assert no orders message is shown")
    public OrdersPage assertNoOrders() {
        $(".empty-state").shouldBe(visible).shouldHave(text("No orders yet"));
        return this;
    }
}
