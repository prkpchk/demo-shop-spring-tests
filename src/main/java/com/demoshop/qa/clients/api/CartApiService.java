package com.demoshop.qa.clients.api;

import com.demoshop.qa.data.dto.api.cart.CartItemRequest;
import com.demoshop.qa.data.dto.api.cart.UpdateCartItemRequest;
import com.demoshop.qa.data.dto.api.cart.CartResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface CartApiService {

    @GET("api/v1/cart")
    Call<CartResponse> getCart();

    @POST("api/v1/cart/items")
    Call<CartResponse> addItem(@Body CartItemRequest request);

    @PUT("api/v1/cart/items/{id}")
    Call<CartResponse> updateItem(@Path("id") Long itemId, @Body UpdateCartItemRequest request);

    @DELETE("api/v1/cart/items/{id}")
    Call<CartResponse> removeItem(@Path("id") Long itemId);
}
