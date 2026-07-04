package com.demoshop.qa.ui.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class ProductPage extends BasePage {

    @Step("Wait for product page to load")
    public ProductPage waitForLoad() {
        $(".product-page").shouldBe(visible);
        $(".product-detail-info h1").shouldBe(visible);
        return this;
    }

    @Step("Set quantity to {quantity}")
    public ProductPage setQuantity(int quantity) {
        $(".quantity-input").setValue(String.valueOf(quantity));
        return this;
    }

    @Step("Click 'Add to Cart'")
    public ProductPage clickAddToCart() {
        $(".add-to-cart .btn-primary").click();
        return this;
    }

    @Step("Wait for 'Added!' confirmation")
    public ProductPage waitForAddedConfirmation() {
        $(".add-to-cart .btn-primary").shouldHave(text("Added!"));
        return this;
    }

    @Step("Get product name")
    public String getProductName() {
        return $(".product-detail-info h1").text();
    }

    @Step("Get product price text")
    public String getPriceText() {
        return $(".product-price-large").text();
    }

    @Step("Assert product name is '{name}'")
    public ProductPage assertProductName(String name) {
        $(".product-detail-info h1").shouldHave(text(name));
        return this;
    }

    @Step("Assert 'Add to Cart' button is visible (product in stock)")
    public ProductPage assertAddToCartVisible() {
        $(".add-to-cart .btn-primary").shouldBe(visible);
        return this;
    }

    @Step("Assert out-of-stock message is shown")
    public ProductPage assertOutOfStock() {
        $(".out-of-stock").shouldBe(visible);
        return this;
    }
}
