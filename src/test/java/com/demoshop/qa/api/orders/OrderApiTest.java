package com.demoshop.qa.api.orders;

import com.demoshop.qa.data.dto.api.cart.CartItemRequest;
import com.demoshop.qa.data.dto.api.user.TopUpRequest;
import com.demoshop.qa.data.dto.api.order.OrderResponse;
import com.demoshop.qa.data.dto.api.common.PageResponse;
import com.demoshop.qa.data.dto.api.product.ProductResponse;
import com.demoshop.qa.data.dto.api.user.UserResponse;
import com.demoshop.qa.clients.api.CartApiService;
import com.demoshop.qa.clients.api.OrderApiService;
import com.demoshop.qa.clients.api.ProductApiService;
import com.demoshop.qa.clients.api.UserApiService;
import com.demoshop.qa.base.BaseApiTest;
import com.demoshop.qa.assertions.DbAssertions;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Response;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Orders")
@Feature("Order Lifecycle")
class OrderApiTest extends BaseApiTest {

    @Autowired
    private DbAssertions dbAssertions;

    @Test
    @Story("Place order")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /orders → 200, order created with PENDING status")
    void placeOrder_success() throws Exception {
        String token = registerAndGetToken();
        topUpBalance(token, BigDecimal.valueOf(500));
        addProductToCart(token);

        OrderApiService orderService = retrofitConfig.createAuthenticatedService(OrderApiService.class, token);

        Response<OrderResponse> response = orderService.placeOrder().execute();

        assertThat(response.code()).as("Place order status").isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().status()).isEqualTo("PENDING");
        assertThat(response.body().items()).isNotEmpty();
        assertThat(response.body().totalAmount()).isGreaterThan(BigDecimal.ZERO);

        dbAssertions.assertOrderStatus(response.body().id(), "PENDING");
    }

    @Test
    @Story("Place order")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /orders with empty cart → 400")
    void placeOrder_emptyCart_returns400() throws Exception {
        String token = registerAndGetToken();
        OrderApiService orderService = retrofitConfig.createAuthenticatedService(OrderApiService.class, token);

        Response<OrderResponse> response = orderService.placeOrder().execute();

        assertThat(response.code()).isEqualTo(400);
    }

    @Test
    @Story("Payment")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /orders/{id}/pay?simulateFailure=false → order moves to PAID")
    void payOrder_success() throws Exception {
        String token = registerAndGetToken();
        topUpBalance(token, BigDecimal.valueOf(500));
        addProductToCart(token);

        OrderApiService orderService = retrofitConfig.createAuthenticatedService(OrderApiService.class, token);
        OrderResponse order = orderService.placeOrder().execute().body();
        assertThat(order).isNotNull();

        Response<OrderResponse> paid = orderService.payOrder(order.id(), false).execute();

        assertThat(paid.code()).as("Payment status").isEqualTo(200);
        assertThat(paid.body().status()).isEqualTo("PAID");

        dbAssertions.assertOrderStatus(order.id(), "PAID");
    }

    @Test
    @Story("Payment")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /orders/{id}/pay?simulateFailure=true → order moves to CANCELLED")
    void payOrder_simulateFailure_cancelled() throws Exception {
        String token = registerAndGetToken();
        topUpBalance(token, BigDecimal.valueOf(500));
        addProductToCart(token);

        OrderApiService orderService = retrofitConfig.createAuthenticatedService(OrderApiService.class, token);
        OrderResponse order = orderService.placeOrder().execute().body();
        assertThat(order).isNotNull();

        Response<OrderResponse> failed = orderService.payOrder(order.id(), true).execute();

        assertThat(failed.code()).isEqualTo(200);
        assertThat(failed.body().status()).isEqualTo("CANCELLED");

        dbAssertions.assertOrderStatus(order.id(), "CANCELLED");
    }

    @Test
    @Story("View orders")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /orders → returns list of user's orders")
    void getOrders_returnsUserOrders() throws Exception {
        String token = registerAndGetToken();
        topUpBalance(token, BigDecimal.valueOf(500));
        addProductToCart(token);

        OrderApiService orderService = retrofitConfig.createAuthenticatedService(OrderApiService.class, token);
        orderService.placeOrder().execute();

        Response<List<OrderResponse>> orders = orderService.getOrders().execute();

        assertThat(orders.code()).isEqualTo(200);
        assertThat(orders.body()).isNotEmpty();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void topUpBalance(String token, BigDecimal amount) throws Exception {
        UserApiService userService = retrofitConfig.createAuthenticatedService(UserApiService.class, token);
        Response<UserResponse> response = userService.topUp(new TopUpRequest(amount)).execute();
        assertThat(response.code()).as("Top-up status").isEqualTo(200);
    }

    private void addProductToCart(String token) throws Exception {
        ProductApiService productService = retrofitConfig.createService(ProductApiService.class);
        Response<PageResponse<ProductResponse>> products = productService.getProducts(0, 1, null).execute();
        assertThat(products.body()).isNotNull();
        Long productId = products.body().content().get(0).id();

        CartApiService cartService = retrofitConfig.createAuthenticatedService(CartApiService.class, token);
        cartService.addItem(new CartItemRequest(productId, 1)).execute();
    }
}
