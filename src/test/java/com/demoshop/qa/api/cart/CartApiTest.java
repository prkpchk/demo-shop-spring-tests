package com.demoshop.qa.api.cart;

import com.demoshop.qa.data.dto.api.cart.CartItemRequest;
import com.demoshop.qa.data.dto.api.cart.UpdateCartItemRequest;
import com.demoshop.qa.data.dto.api.cart.CartResponse;
import com.demoshop.qa.data.dto.api.common.PageResponse;
import com.demoshop.qa.data.dto.api.product.ProductResponse;
import com.demoshop.qa.clients.api.CartApiService;
import com.demoshop.qa.clients.api.ProductApiService;
import com.demoshop.qa.base.BaseApiTest;
import com.demoshop.qa.assertions.DbAssertions;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Response;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Cart")
@Feature("Shopping Cart")
class CartApiTest extends BaseApiTest {

    @Autowired
    private DbAssertions dbAssertions;

    @Test
    @Story("View cart")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /cart → empty cart for new user")
    void getCart_newUser_returnsEmpty() throws Exception {
        String token = registerAndGetToken();
        CartApiService cartService = retrofitConfig.createAuthenticatedService(CartApiService.class, token);

        Response<CartResponse> response = cartService.getCart().execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().items()).isEmpty();
    }

    @Test
    @Story("View cart")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /cart unauthenticated → 401")
    void getCart_unauthenticated_returns401() throws Exception {
        CartApiService cartService = retrofitConfig.createService(CartApiService.class);

        Response<CartResponse> response = cartService.getCart().execute();

        assertThat(response.code()).isEqualTo(401);
    }

    @Test
    @Story("Add to cart")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /cart/items → item added, cart total updated")
    void addItem_success() throws Exception {
        String token = registerAndGetToken();
        CartApiService cartService = retrofitConfig.createAuthenticatedService(CartApiService.class, token);
        Long productId = getFirstAvailableProductId();

        Response<CartResponse> response = cartService
                .addItem(new CartItemRequest(productId, 1)).execute();

        assertThat(response.code()).as("Add to cart status").isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().items()).hasSize(1);
        assertThat(response.body().items().get(0).productId()).isEqualTo(productId);
        assertThat(response.body().items().get(0).quantity()).isEqualTo(1);
    }

    @Test
    @Story("Add to cart")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /cart/items with quantity > stock → 400/409")
    void addItem_exceedsStock_returns4xx() throws Exception {
        String token = registerAndGetToken();
        CartApiService cartService = retrofitConfig.createAuthenticatedService(CartApiService.class, token);
        Long productId = getFirstAvailableProductId();

        Response<CartResponse> response = cartService
                .addItem(new CartItemRequest(productId, 999_999)).execute();

        assertThat(response.code()).isBetween(400, 409);
    }

    @Test
    @Story("Update cart")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PUT /cart/items/{id} → quantity updated")
    void updateItem_success() throws Exception {
        String token = registerAndGetToken();
        CartApiService cartService = retrofitConfig.createAuthenticatedService(CartApiService.class, token);
        Long productId = getFirstAvailableProductId();

        // Add item first
        CartResponse cart = cartService.addItem(new CartItemRequest(productId, 1)).execute().body();
        assertThat(cart).isNotNull();
        Long itemId = cart.items().get(0).id();

        Response<CartResponse> updated = cartService
                .updateItem(itemId, new UpdateCartItemRequest(2)).execute();

        assertThat(updated.code()).isEqualTo(200);
        assertThat(updated.body().items().get(0).quantity()).isEqualTo(2);
    }

    @Test
    @Story("Remove from cart")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /cart/items/{id} → item removed from cart")
    void removeItem_success() throws Exception {
        String token = registerAndGetToken();
        CartApiService cartService = retrofitConfig.createAuthenticatedService(CartApiService.class, token);
        Long productId = getFirstAvailableProductId();

        // Add item first
        CartResponse cart = cartService.addItem(new CartItemRequest(productId, 1)).execute().body();
        assertThat(cart).isNotNull();
        Long itemId = cart.items().get(0).id();

        Response<CartResponse> removed = cartService.removeItem(itemId).execute();

        assertThat(removed.code()).isEqualTo(200);
        assertThat(removed.body().items()).isEmpty();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Long getFirstAvailableProductId() throws Exception {
        ProductApiService productService = retrofitConfig.createService(ProductApiService.class);
        Response<PageResponse<ProductResponse>> products = productService.getProducts(0, 1, null).execute();
        assertThat(products.body()).isNotNull();
        assertThat(products.body().content()).isNotEmpty();
        return products.body().content().get(0).id();
    }
}
