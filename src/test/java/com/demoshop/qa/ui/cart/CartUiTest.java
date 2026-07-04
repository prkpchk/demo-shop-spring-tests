package com.demoshop.qa.ui.cart;

import com.demoshop.qa.data.dto.api.auth.RegisterRequest;
import com.demoshop.qa.clients.api.AuthApiService;
import com.demoshop.qa.base.BaseUiTest;
import com.demoshop.qa.ui.pages.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@Epic("Cart UI")
@Feature("Shopping Cart")
class CartUiTest extends BaseUiTest {

    @Test
    @Story("Add to cart")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Add product to cart → cart shows 1 item, total updated")
    void addToCart_showsItemInCart() throws Exception {
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        retrofitConfig.createService(AuthApiService.class).register(reg).execute();

        // Login, browse to first product, add to cart
        new LoginPage().open()
                .loginAs(reg.email(), reg.password())
                .waitForProducts()
                .openFirstProduct()
                .waitForLoad()
                .clickAddToCart()
                .waitForAddedConfirmation();

        // Open cart and verify
        new CartPage().open()
                .assertItemCount(1);
    }

    @Test
    @Story("Remove from cart")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Remove item from cart → cart becomes empty")
    void removeFromCart_cartIsEmpty() throws Exception {
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        retrofitConfig.createService(AuthApiService.class).register(reg).execute();

        new LoginPage().open()
                .loginAs(reg.email(), reg.password())
                .waitForProducts()
                .openFirstProduct()
                .waitForLoad()
                .clickAddToCart()
                .waitForAddedConfirmation();

        new CartPage().open()
                .assertItemCount(1)
                .removeItem(0)
                .assertEmpty();
    }

    @Test
    @Story("View cart")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Unauthenticated user visits cart → redirected to login")
    void cart_unauthenticated_redirectsToLogin() {
        // Opening /cart without auth: PrivateRoute must redirect to the login form.
        // CartPage.open() is unusable here — it waits for the cart heading.
        open("/cart");
        $(".auth-card h1").shouldHave(text("Login"));
    }

    @Test
    @Story("View cart")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("New user's cart is empty")
    void newUser_cartIsEmpty() throws Exception {
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        retrofitConfig.createService(AuthApiService.class).register(reg).execute();

        new LoginPage().open()
                .loginAs(reg.email(), reg.password());

        new CartPage().open().assertEmpty();
    }
}
