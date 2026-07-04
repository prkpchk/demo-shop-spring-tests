package com.demoshop.qa.clients.api;

import com.demoshop.qa.data.dto.api.auth.LoginRequest;
import com.demoshop.qa.data.dto.api.auth.RegisterRequest;
import com.demoshop.qa.data.dto.api.auth.AuthResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("api/v1/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("api/v1/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);
}
