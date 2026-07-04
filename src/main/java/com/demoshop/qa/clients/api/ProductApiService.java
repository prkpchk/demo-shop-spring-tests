package com.demoshop.qa.clients.api;

import com.demoshop.qa.data.dto.api.product.ProductRequest;
import com.demoshop.qa.data.dto.api.common.PageResponse;
import com.demoshop.qa.data.dto.api.product.ProductResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductApiService {

    @GET("api/v1/products")
    Call<PageResponse<ProductResponse>> getProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("category") String category
    );

    @GET("api/v1/products/{id}")
    Call<ProductResponse> getProduct(@Path("id") Long id);

    @POST("api/v1/products")
    Call<ProductResponse> createProduct(@Body ProductRequest request);

    @PUT("api/v1/products/{id}")
    Call<ProductResponse> updateProduct(@Path("id") Long id, @Body ProductRequest request);

    @DELETE("api/v1/products/{id}")
    Call<Void> deleteProduct(@Path("id") Long id);
}
