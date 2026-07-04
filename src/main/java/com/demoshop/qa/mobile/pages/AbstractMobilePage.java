package com.demoshop.qa.mobile.pages;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Abstract base class for all mobile page objects.
 * Provides common interactions like tap, scroll, and waiting.
 */
public abstract class AbstractMobilePage {

    @Step("Tap element by accessibility ID '{id}'")
    protected void tapByAccessibilityId(String id) {
        $(AppiumBy.accessibilityId(id)).shouldBe(visible).click();
    }

    @Step("Tap element by ID '{id}'")
    protected void tapById(String id) {
        $(AppiumBy.id(id)).shouldBe(visible).click();
    }

    @Step("Enter text '{text}' into field by accessibility ID '{id}'")
    protected void typeByAccessibilityId(String id, String text) {
        $(AppiumBy.accessibilityId(id)).shouldBe(visible).setValue(text);
    }

    @Step("Enter text '{text}' into field by ID '{id}'")
    protected void typeById(String id, String text) {
        $(AppiumBy.id(id)).shouldBe(visible).setValue(text);
    }

    @Step("Get text of element by accessibility ID '{id}'")
    protected String getTextByAccessibilityId(String id) {
        return $(AppiumBy.accessibilityId(id)).shouldBe(visible).text();
    }

    @Step("Get text of element by ID '{id}'")
    protected String getTextById(String id) {
        return $(AppiumBy.id(id)).shouldBe(visible).text();
    }

    @Step("Assert element with accessibility ID '{id}' is visible")
    protected void assertVisibleByAccessibilityId(String id) {
        $(AppiumBy.accessibilityId(id)).shouldBe(visible);
    }

    @Step("Assert element with accessibility ID '{id}' has text '{text}'")
    protected void assertTextByAccessibilityId(String id, String text) {
        $(AppiumBy.accessibilityId(id)).shouldHave(text(text));
    }

    @Step("Scroll down on screen")
    protected void scrollDown() {
        AppiumDriver driver = (AppiumDriver) WebDriverRunner.getWebDriver();
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        int startY = (int) (size.height * 0.7);
        int endY = (int) (size.height * 0.3);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), x, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(swipe));
    }

    @Step("Wait for element with accessibility ID '{id}'")
    protected void waitForVisible(String id) {
        $(AppiumBy.accessibilityId(id)).shouldBe(visible);
    }

    @Step("Wait for element with locator to be visible")
    protected void waitForVisible(By locator) {
        $(locator).shouldBe(visible);
    }

    @Step("Check if element by accessibility ID '{id}' is displayed")
    protected boolean isDisplayed(String accessibilityId) {
        return $(AppiumBy.accessibilityId(accessibilityId)).is(visible);
    }
}
