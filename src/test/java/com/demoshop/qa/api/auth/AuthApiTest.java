package com.demoshop.qa.api.auth;

import com.demoshop.qa.data.dto.api.auth.LoginRequest;
import com.demoshop.qa.data.dto.api.auth.RegisterRequest;
import com.demoshop.qa.data.dto.api.auth.AuthResponse;
import com.demoshop.qa.clients.api.AuthApiService;
import com.demoshop.qa.base.BaseApiTest;
import com.demoshop.qa.assertions.DbAssertions;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Response;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Auth")
@Feature("Registration & Login")
class AuthApiTest extends BaseApiTest {

    @Autowired
    private DbAssertions dbAssertions;

    // ─── Registration ─────────────────────────────────────────────────────────

    @Test
    @Story("Registration")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /auth/register → 200, returns JWT token and USER role")
    void register_success() throws Exception {
        RegisterRequest req = fakerUtils.randomRegisterRequest();
        AuthApiService service = retrofitConfig.createService(AuthApiService.class);

        Response<AuthResponse> response = service.register(req).execute();

        assertThat(response.code()).as("HTTP status").isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().token()).as("JWT token").isNotBlank();
        assertThat(response.body().email()).isEqualTo(req.email());
        assertThat(response.body().role()).isEqualTo("USER");

        dbAssertions.assertUserExists(req.email());
        dbAssertions.assertUserRole(req.email(), "USER");
    }

    @Test
    @Story("Registration")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /auth/register with duplicate email → 400/409")
    void register_duplicateEmail_returns4xx() throws Exception {
        RegisterRequest req = fakerUtils.randomRegisterRequest();
        AuthApiService service = retrofitConfig.createService(AuthApiService.class);

        service.register(req).execute(); // first registration

        Response<AuthResponse> duplicate = service.register(req).execute();

        assertThat(duplicate.code())
                .as("Duplicate email should be rejected")
                .isBetween(400, 409);
    }

    @Test
    @Story("Registration")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /auth/register with missing password → 400")
    void register_missingPassword_returns400() throws Exception {
        AuthApiService service = retrofitConfig.createService(AuthApiService.class);
        RegisterRequest req = new RegisterRequest(fakerUtils.randomName(), fakerUtils.randomEmail(), "");

        Response<AuthResponse> response = service.register(req).execute();

        assertThat(response.code()).as("Empty password should be rejected").isEqualTo(400);
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Test
    @Story("Login")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /auth/login → 200, returns JWT token")
    void login_success() throws Exception {
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        AuthApiService service = retrofitConfig.createService(AuthApiService.class);
        service.register(reg).execute();

        Response<AuthResponse> response = service
                .login(new LoginRequest(reg.email(), reg.password())).execute();

        assertThat(response.code()).as("HTTP status").isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().token()).isNotBlank();
        assertThat(response.body().email()).isEqualTo(reg.email());
    }

    @Test
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /auth/login with wrong password → 401/400")
    void login_wrongPassword_returns4xx() throws Exception {
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        AuthApiService service = retrofitConfig.createService(AuthApiService.class);
        service.register(reg).execute();

        Response<AuthResponse> response = service
                .login(new LoginRequest(reg.email(), "wrong-password")).execute();

        assertThat(response.code())
                .as("Wrong password should be rejected")
                .isBetween(400, 401);
    }

    @Test
    @Story("Login")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /auth/login with unknown email → 400/401")
    void login_unknownEmail_returns4xx() throws Exception {
        AuthApiService service = retrofitConfig.createService(AuthApiService.class);

        Response<AuthResponse> response = service
                .login(new LoginRequest("nobody@example.com", "pass123")).execute();

        assertThat(response.code()).isBetween(400, 401);
    }
}
