package com.demoshop.qa.clients.api;

import com.demoshop.qa.data.dto.api.user.TopUpRequest;
import com.demoshop.qa.data.dto.api.user.UserResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApiService {

    @GET("api/v1/users/me")
    Call<UserResponse> getProfile();

    @PUT("api/v1/users/me")
    Call<UserResponse> updateProfile(@Body java.util.Map<String, String> body);

    @POST("api/v1/users/me/top-up")
    Call<UserResponse> topUp(@Body TopUpRequest request);
}
