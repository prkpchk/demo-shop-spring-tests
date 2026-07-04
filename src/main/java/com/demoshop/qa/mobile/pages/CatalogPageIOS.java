package com.demoshop.qa.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * iOS catalog page — lists products using XCUITest accessibility-id and class-chain locators.
 */
public class CatalogPageIOS extends AbstractMobilePage {

    private static final String CATALOG_TITLE = "catalog_title";
    private static final String PRODUCT_CELL  = "product_cell";
    private static final String PRODUCT_NAME  = "product_name_label";

    @Step("Verify catalog is displayed")
    public CatalogPageIOS verifyDisplayed() {
        $(AppiumBy.accessibilityId(CATALOG_TITLE)).shouldBe(visible);
        return this;
    }

    @Step("Wait for product list to appear")
    public CatalogPageIOS waitForProducts() {
        $$(AppiumBy.accessibilityId(PRODUCT_CELL)).shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Assert at least {count} products visible")
    public CatalogPageIOS assertProductCountAtLeast(int count) {
        $$(AppiumBy.accessibilityId(PRODUCT_CELL)).shouldHave(sizeGreaterThanOrEqual(count));
        return this;
    }

    @Step("Tap first product")
    public void tapFirstProduct() {
        $$(AppiumBy.accessibilityId(PRODUCT_CELL)).first().shouldBe(visible).click();
    }

    @Step("Tap product named '{name}' using class chain")
    public void tapProductByName(String name) {
        $(AppiumBy.iOSClassChain(
                "**/XCUIElementTypeCell[`name == '" + name + "'`]"
        )).shouldBe(visible).click();
    }

    @Step("Scroll and check for more products")
    public CatalogPageIOS scrollAndVerify() {
        scrollDown();
        $(AppiumBy.accessibilityId(CATALOG_TITLE)).shouldBe(visible);
        return this;
    }
}
