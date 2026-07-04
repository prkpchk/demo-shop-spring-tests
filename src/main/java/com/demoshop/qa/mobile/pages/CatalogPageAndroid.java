package com.demoshop.qa.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Android catalog page — lists products using UIAutomator2 resource-id locators.
 */
public class CatalogPageAndroid extends AbstractMobilePage {

    private static final String PRODUCT_LIST   = "com.demoshop:id/rv_products";
    private static final String PRODUCT_ITEM   = "com.demoshop:id/item_product";
    private static final String PRODUCT_NAME   = "com.demoshop:id/tv_product_name";
    private static final String CATALOG_TITLE  = "com.demoshop:id/tv_catalog_title";

    @Step("Verify catalog is displayed")
    public CatalogPageAndroid verifyDisplayed() {
        $(AppiumBy.id(CATALOG_TITLE)).shouldBe(visible);
        return this;
    }

    @Step("Wait for product list to appear")
    public CatalogPageAndroid waitForProducts() {
        $(AppiumBy.id(PRODUCT_LIST)).shouldBe(visible);
        return this;
    }

    @Step("Assert at least {count} products visible")
    public CatalogPageAndroid assertProductCountAtLeast(int count) {
        $$(AppiumBy.id(PRODUCT_ITEM)).shouldHave(sizeGreaterThanOrEqual(count));
        return this;
    }

    @Step("Tap first product in list")
    public void tapFirstProduct() {
        $$(AppiumBy.id(PRODUCT_ITEM)).first().shouldBe(visible).click();
    }

    @Step("Tap product named '{name}'")
    public void tapProductByName(String name) {
        $$(AppiumBy.id(PRODUCT_NAME)).findBy(text(name)).click();
    }

    @Step("Scroll down and check for more products")
    public CatalogPageAndroid scrollAndVerify() {
        scrollDown();
        $(AppiumBy.id(PRODUCT_LIST)).shouldBe(visible);
        return this;
    }
}
