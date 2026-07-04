package com.demoshop.qa.clients.api;

import com.demoshop.qa.data.dto.api.order.OrderResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface OrderApiService {

    @POST("api/v1/orders")
    Call<OrderResponse> placeOrder();

    @GET("api/v1/orders")
    Call<List<OrderResponse>> getOrders();

    @GET("api/v1/orders/{id}")
    Call<OrderResponse> getOrder(@Path("id") Long id);

    @POST("api/v1/orders/{id}/pay")
    Call<OrderResponse> payOrder(
            @Path("id") Long id,
            @Query("simulateFailure") boolean simulateFailure
    );
}
