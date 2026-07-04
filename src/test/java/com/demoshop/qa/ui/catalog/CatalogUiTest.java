package com.demoshop.qa.ui.catalog;

import com.demoshop.qa.base.BaseUiTest;
import com.demoshop.qa.ui.pages.CatalogPage;
import com.demoshop.qa.ui.pages.ProductPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Catalog UI")
@Feature("Product Browsing")
class CatalogUiTest extends BaseUiTest {

    @Test
    @Story("View catalog")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Catalog page loads with products visible")
    void catalog_loadsWithProducts() {
        new CatalogPage().open()
                .assertProductCountAtLeast(1);
    }

    @Test
    @Story("Category filter")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Filter by Electronics → only Electronics products shown")
    void catalog_filterByElectronics_showsOnlyElectronics() {
        new CatalogPage().open()
                .filterByCategory("Electronics")
                .assertCategoryActive("Electronics")
                .assertAllProductsHaveCategory("Electronics");
    }

    @Test
    @Story("Category filter")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Click 'All' after category filter → all products shown")
    void catalog_allFilter_showsAllProducts() {
        CatalogPage page = new CatalogPage().open();
        int totalCount = page.productCards().size();
        page.filterByCategory("Electronics");
        page.showAll();
        page.assertProductCountAtLeast(totalCount);
    }

    @Test
    @Story("Product navigation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Click product card → navigates to product detail page")
    void catalog_clickProductCard_opensProductPage() {
        ProductPage product = new CatalogPage().open()
                .waitForProducts()
                .openFirstProduct();

        product.waitForLoad();
        product.assertAddToCartVisible();
    }
}
