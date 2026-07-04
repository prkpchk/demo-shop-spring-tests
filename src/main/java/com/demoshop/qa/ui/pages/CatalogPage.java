package com.demoshop.qa.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CatalogPage extends BasePage {

    private static final String PATH = "/";

    @Step("Open catalog page")
    public CatalogPage open() {
        navigateTo(PATH);
        $(".catalog-page h1").shouldHave(text("Products"));
        return this;
    }

    @Step("Filter by category '{category}'")
    public CatalogPage filterByCategory(String category) {
        $$(".btn-chip").findBy(text(category)).click();
        return this;
    }

    @Step("Click 'All' category filter")
    public CatalogPage showAll() {
        $$(".btn-chip").findBy(text("All")).click();
        return this;
    }

    @Step("Wait for at least one product card")
    public CatalogPage waitForProducts() {
        $$(".product-card").shouldHave(sizeGreaterThan(0));
        return this;
    }

    public ElementsCollection productCards() {
        return $$(".product-card");
    }

    @Step("Click first product card")
    public ProductPage openFirstProduct() {
        $$(".product-card").first().click();
        return new ProductPage();
    }

    @Step("Click product card with name containing '{name}'")
    public ProductPage openProductByName(String name) {
        $$(".product-card").findBy(text(name)).click();
        return new ProductPage();
    }

    @Step("Assert at least {count} products are shown")
    public CatalogPage assertProductCountAtLeast(int count) {
        $$(".product-card").shouldHave(sizeGreaterThanOrEqual(count));
        return this;
    }

    @Step("Assert category filter '{category}' is active")
    public CatalogPage assertCategoryActive(String category) {
        $$(".btn-chip").findBy(text(category)).shouldHave(cssClass("active"));
        return this;
    }

    @Step("Assert all visible products have category '{category}'")
    public CatalogPage assertAllProductsHaveCategory(String category) {
        $$(".product-category").forEach(el -> el.shouldHave(text(category)));
        return this;
    }
}
