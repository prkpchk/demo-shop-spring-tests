package com.demoshop.qa.ui.auth;

import com.demoshop.qa.data.dto.api.auth.RegisterRequest;
import com.demoshop.qa.clients.api.AuthApiService;
import com.demoshop.qa.base.BaseUiTest;
import com.demoshop.qa.ui.pages.CatalogPage;
import com.demoshop.qa.ui.pages.LoginPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Authentication UI")
@Feature("Login")
class LoginUiTest extends BaseUiTest {

    @Test
    @Story("Successful login")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Login with valid credentials → redirected to catalog, name in navbar")
    void login_validCredentials_redirectsToCatalog() throws Exception {
        // Register via API to get a known user
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        retrofitConfig.createService(AuthApiService.class).register(reg).execute();

        LoginPage loginPage = new LoginPage().open();
        CatalogPage catalog = loginPage.loginAs(reg.email(), reg.password());

        catalog.waitForProducts();
        catalog.navbar().assertLoggedIn();
        catalog.navbar().assertUserName(reg.name());
    }

    @Test
    @Story("Login validation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Login with wrong password → error message shown")
    void login_wrongPassword_showsError() throws Exception {
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        retrofitConfig.createService(AuthApiService.class).register(reg).execute();

        new LoginPage().open()
                .enterEmail(reg.email())
                .enterPassword("wrong-password")
                .clickLoginExpectFailure()
                .assertError("Invalid credentials");
    }

    @Test
    @Story("Login validation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Login with unknown email → error message shown")
    void login_unknownEmail_showsError() {
        new LoginPage().open()
                .enterEmail("nobody_" + System.nanoTime() + "@example.com")
                .enterPassword("pass123")
                .clickLoginExpectFailure();
    }

    @Test
    @Story("Navigation")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Login page has link to register")
    void loginPage_hasRegisterLink() {
        new LoginPage().open().goToRegister();
    }

    @Test
    @Story("Logout")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Logout → redirected to login, navbar shows Login link")
    void logout_redirectsToLogin() throws Exception {
        RegisterRequest reg = fakerUtils.randomRegisterRequest();
        retrofitConfig.createService(AuthApiService.class).register(reg).execute();

        CatalogPage catalog = new LoginPage().open().loginAs(reg.email(), reg.password());
        catalog.waitForProducts();

        LoginPage login = catalog.navbar().logout();
        login.open(); // verifies redirect to login page
        login.navbar().assertNotLoggedIn();
    }
}
