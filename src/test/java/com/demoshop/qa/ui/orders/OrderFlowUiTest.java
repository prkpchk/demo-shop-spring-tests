package com.demoshop.qa.ui.orders;

import com.demoshop.qa.data.dto.api.cart.CartItemRequest;
import com.demoshop.qa.data.dto.api.auth.RegisterRequest;
import com.demoshop.qa.data.dto.api.user.TopUpRequest;
import com.demoshop.qa.data.dto.api.auth.AuthResponse;
import com.demoshop.qa.data.dto.api.common.PageResponse;
import com.demoshop.qa.data.dto.api.product.ProductResponse;
import com.demoshop.qa.clients.api.AuthApiService;
import com.demoshop.qa.clients.api.CartApiService;
import com.demoshop.qa.clients.api.ProductApiService;
import com.demoshop.qa.clients.api.UserApiService;
import com.demoshop.qa.base.BaseUiTest;
import com.demoshop.qa.assertions.DbAssertions;
import com.demoshop.qa.ui.pages.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Response;

import java.math.BigDecimal;

@Epic("Order Flow UI")
@Feature("End-to-End Purchase")
class OrderFlowUiTest extends BaseUiTest {

    @Autowired
    private DbAssertions dbAssertions;

    @Test
    @Story("Full purchase flow")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Full flow: register → top-up → add to cart → place order → pay → PAID")
    void fullPurchaseFlow_orderIsPaid() throws Exception {
        // ── 1. Register via API and top-up balance ────────────────────────────
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        AuthApiService authService = retrofitConfig.createService(AuthApiService.class);
        Response<AuthResponse> authResp = authService.register(reg).execute();
        String token = authResp.body().token();

        retrofitConfig.createAuthenticatedService(UserApiService.class, token)
                .topUp(new TopUpRequest(BigDecimal.valueOf(2000))).execute();

        // ── 2. Add first product to cart via API ──────────────────────────────
        ProductApiService productService = retrofitConfig.createService(ProductApiService.class);
        Response<PageResponse<ProductResponse>> products = productService.getProducts(0, 1, null).execute();
        Long productId = products.body().content().get(0).id();

        retrofitConfig.createAuthenticatedService(CartApiService.class, token)
                .addItem(new CartItemRequest(productId, 1)).execute();

        // ── 3. Login via UI ───────────────────────────────────────────────────
        new LoginPage().open().loginAs(reg.email(), reg.password());

        // ── 4. Go to cart and place order ─────────────────────────────────────
        OrdersPage ordersPage = new CartPage().open()
                .assertItemCount(1)
                .placeOrder()
                .waitForLoad();

        ordersPage.assertOrderCountAtLeast(1);
        ordersPage.assertFirstOrderStatus("PENDING");

        // ── 5. Pay the order ──────────────────────────────────────────────────
        ordersPage.clickPayForFirstPendingOrder()
                .waitForModal()
                .clickPayNow()
                .waitForLoad()
                .assertFirstOrderStatus("PAID");

        // ── 6. DB verification ────────────────────────────────────────────────
        dbAssertions.assertUserHasOrders(reg.email(), 1);
    }

    @Test
    @Story("Payment failure simulation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Simulate payment failure → order moves to CANCELLED")
    void payOrder_simulateFailure_orderIsCancelled() throws Exception {
        // Setup via API
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        AuthResponse auth = retrofitConfig.createService(AuthApiService.class)
                .register(reg).execute().body();
        String token = auth.token();

        retrofitConfig.createAuthenticatedService(UserApiService.class, token)
                .topUp(new TopUpRequest(BigDecimal.valueOf(2000))).execute();

        PageResponse<ProductResponse> products = retrofitConfig.createService(ProductApiService.class)
                .getProducts(0, 1, null).execute().body();
        Long productId = products.content().get(0).id();

        retrofitConfig.createAuthenticatedService(CartApiService.class, token)
                .addItem(new CartItemRequest(productId, 1)).execute();

        // UI flow
        new LoginPage().open().loginAs(reg.email(), reg.password());

        new CartPage().open()
                .placeOrder()
                .waitForLoad()
                .clickPayForFirstPendingOrder()
                .waitForModal()
                .toggleSimulateFailure()
                .clickSimulateFailure()
                .waitForLoad()
                .assertFirstOrderStatus("CANCELLED");
    }

    @Test
    @Story("Profile top-up UI")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Top up balance via profile page → balance increases")
    void topUp_viaProfilePage_balanceIncreases() throws Exception {
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        retrofitConfig.createService(AuthApiService.class).register(reg).execute();

        new LoginPage().open().loginAs(reg.email(), reg.password());

        ProfilePage profile = new ProfilePage().open().waitForLoad();
        String balanceBefore = profile.getBalanceText();

        profile.enterTopUpAmount("100")
                .clickTopUp()
                .assertTopUpSuccess();

        dbAssertions.assertUserBalanceAtLeast(reg.email(), BigDecimal.valueOf(100));
    }
}
