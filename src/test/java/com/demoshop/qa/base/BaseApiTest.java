package com.demoshop.qa.base;

import com.demoshop.qa.data.dto.api.auth.LoginRequest;
import com.demoshop.qa.data.dto.api.auth.RegisterRequest;
import com.demoshop.qa.data.dto.api.auth.AuthResponse;
import com.demoshop.qa.clients.api.AuthApiService;
import com.demoshop.qa.core.config.RetrofitConfig;
import com.demoshop.qa.dataproviders.FakerUtils;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Response;

import java.io.IOException;

/**
 * Base class for all API tests. Provides helpers for authentication
 * and a pre-configured RetrofitConfig for building service clients.
 */
public abstract class BaseApiTest extends BaseTest {

    @Autowired
    protected RetrofitConfig retrofitConfig;

    @Autowired
    protected FakerUtils fakerUtils;

    // ─── Auth helpers ──────────────────────────────────────────────────────────

    @Step("Register a new random user and return the auth token")
    protected String registerAndGetToken() throws IOException {
        RegisterRequest request = fakerUtils.randomRegisterRequest();
        AuthApiService authService = retrofitConfig.createService(AuthApiService.class);
        Response<AuthResponse> response = authService.register(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IllegalStateException("Registration failed: " + response.code());
        }
        return response.body().token();
    }

    @Step("Login as '{email}' and return the auth token")
    protected String loginAndGetToken(String email, String password) throws IOException {
        AuthApiService authService = retrofitConfig.createService(AuthApiService.class);
        Response<AuthResponse> response = authService
                .login(new LoginRequest(email, password)).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IllegalStateException("Login failed: " + response.code());
        }
        return response.body().token();
    }

    @Step("Register random user and return full auth response")
    protected AuthResponse registerUser() throws IOException {
        RegisterRequest request = fakerUtils.randomRegisterRequest();
        AuthApiService authService = retrofitConfig.createService(AuthApiService.class);
        Response<AuthResponse> response = authService.register(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IllegalStateException("Registration failed: " + response.code());
        }
        return response.body();
    }
}
