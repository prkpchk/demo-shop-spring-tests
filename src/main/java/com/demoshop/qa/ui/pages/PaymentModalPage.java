package com.demoshop.qa.ui.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class PaymentModalPage extends BasePage {

    @Step("Wait for payment modal to appear")
    public PaymentModalPage waitForModal() {
        $(".modal").shouldBe(visible);
        return this;
    }

    @Step("Toggle 'Simulate payment failure' checkbox")
    public PaymentModalPage toggleSimulateFailure() {
        $(".checkbox-label input[type='checkbox']").click();
        return this;
    }

    @Step("Click 'Pay Now'")
    public OrdersPage clickPayNow() {
        $(".modal-actions .btn-primary").shouldHave(text("Pay Now")).click();
        $(".modal").shouldNotBe(visible);
        return new OrdersPage();
    }

    @Step("Click 'Simulate Failure'")
    public OrdersPage clickSimulateFailure() {
        $(".modal-actions .btn-danger").shouldHave(text("Simulate Failure")).click();
        $(".modal").shouldNotBe(visible);
        return new OrdersPage();
    }

    @Step("Click 'Cancel'")
    public OrdersPage clickCancel() {
        $(".btn-secondary").shouldHave(text("Cancel")).click();
        $(".modal").shouldNotBe(visible);
        return new OrdersPage();
    }

    @Step("Assert modal shows order amount containing '{amount}'")
    public PaymentModalPage assertAmount(String amount) {
        $(".modal-amount").shouldHave(text(amount));
        return this;
    }
}
