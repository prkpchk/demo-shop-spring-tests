package com.demoshop.qa.api.products;

import com.demoshop.qa.data.dto.api.product.ProductRequest;
import com.demoshop.qa.data.dto.api.common.PageResponse;
import com.demoshop.qa.data.dto.api.product.ProductResponse;
import com.demoshop.qa.clients.api.ProductApiService;
import com.demoshop.qa.base.BaseApiTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Products")
@Feature("Product Catalog")
class ProductApiTest extends BaseApiTest {

    private static final String ADMIN_EMAIL = "admin@demo.shop";
    private static final String ADMIN_PASSWORD = "admin123";

    // ─── Public endpoints ─────────────────────────────────────────────────────

    @Test
    @Story("Browse catalog")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET /products → 200, returns paginated product list")
    void getProducts_public_returns200() throws Exception {
        ProductApiService service = retrofitConfig.createService(ProductApiService.class);

        Response<PageResponse<ProductResponse>> response =
                service.getProducts(0, 10, null).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().content()).isNotNull();
    }

    @Test
    @Story("Browse catalog")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /products?category=Electronics → filters by category")
    void getProducts_byCategory_filtersCorrectly() throws Exception {
        ProductApiService service = retrofitConfig.createService(ProductApiService.class);

        Response<PageResponse<ProductResponse>> response =
                service.getProducts(0, 20, "Electronics").execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        response.body().content().forEach(p ->
                assertThat(p.category()).isEqualTo("Electronics"));
    }

    @Test
    @Story("Browse catalog")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /products/{id} → 200, returns product details")
    void getProduct_byId_returns200() throws Exception {
        ProductApiService service = retrofitConfig.createService(ProductApiService.class);

        // Fetch first product from catalog
        PageResponse<ProductResponse> page = service.getProducts(0, 1, null).execute().body();
        assertThat(page).isNotNull();
        assertThat(page.content()).isNotEmpty();
        Long firstId = page.content().get(0).id();

        Response<ProductResponse> response = service.getProduct(firstId).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().id()).isEqualTo(firstId);
    }

    @Test
    @Story("Browse catalog")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /products/{id} with non-existent id → 404")
    void getProduct_notFound_returns404() throws Exception {
        ProductApiService service = retrofitConfig.createService(ProductApiService.class);

        Response<ProductResponse> response = service.getProduct(999_999L).execute();

        assertThat(response.code()).isEqualTo(404);
    }

    // ─── Admin endpoints ──────────────────────────────────────────────────────

    @Test
    @Story("Admin product management")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /products as ADMIN → 200/201, creates product")
    void createProduct_asAdmin_succeeds() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        ProductApiService service = retrofitConfig.createAuthenticatedService(ProductApiService.class, token);

        ProductRequest request = new ProductRequest(
                fakerUtils.randomProductName(),
                fakerUtils.randomProductDescription(),
                fakerUtils.randomPrice(),
                fakerUtils.randomStock(),
                fakerUtils.randomCategory(),
                null
        );

        Response<ProductResponse> response = service.createProduct(request).execute();

        assertThat(response.code()).as("Admin create product").isBetween(200, 201);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().id()).isNotNull();
        assertThat(response.body().name()).isEqualTo(request.name());
    }

    @Test
    @Story("Admin product management")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /products without auth → 401/403")
    void createProduct_unauthenticated_returns401() throws Exception {
        ProductApiService service = retrofitConfig.createService(ProductApiService.class);

        Response<ProductResponse> response = service.createProduct(
                new ProductRequest("Item", "Desc", BigDecimal.TEN, 10, "Home", null)
        ).execute();

        assertThat(response.code()).isBetween(401, 403);
    }

    @Test
    @Story("Admin product management")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /products as regular USER → 401/403")
    void createProduct_asUser_isDenied() throws Exception {
        String token = registerAndGetToken();
        ProductApiService service = retrofitConfig.createAuthenticatedService(ProductApiService.class, token);

        Response<ProductResponse> response = service.createProduct(
                new ProductRequest("Item", "Desc", BigDecimal.TEN, 10, "Home", null)
        ).execute();

        // The app replies 401 (not the canonical 403): its security chain routes
        // AccessDeniedException from @PreAuthorize into the 401 entry point
        assertThat(response.code()).isBetween(401, 403);
    }

    @Test
    @Story("Admin product management")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("DELETE /products/{id} as ADMIN → 200/204")
    void deleteProduct_asAdmin_succeeds() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        ProductApiService service = retrofitConfig.createAuthenticatedService(ProductApiService.class, token);

        // Create product first
        ProductRequest request = new ProductRequest(
                fakerUtils.randomProductName(), "To be deleted",
                BigDecimal.TEN, 5, "Home", null);
        ProductResponse created = service.createProduct(request).execute().body();
        assertThat(created).isNotNull();

        Response<Void> deleteResponse = service.deleteProduct(created.id()).execute();

        assertThat(deleteResponse.code()).isBetween(200, 204);

        // Verify gone
        Response<ProductResponse> getResponse = service.getProduct(created.id()).execute();
        assertThat(getResponse.code()).isEqualTo(404);
    }
}
