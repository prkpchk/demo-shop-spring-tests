package com.demoshop.qa.ui.auth;

import com.demoshop.qa.base.BaseUiTest;
import com.demoshop.qa.assertions.DbAssertions;
import com.demoshop.qa.ui.pages.CatalogPage;
import com.demoshop.qa.ui.pages.RegisterPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Epic("Authentication UI")
@Feature("Registration")
class RegisterUiTest extends BaseUiTest {

    @Autowired
    private DbAssertions dbAssertions;

    @Test
    @Story("Successful registration")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Register with valid data → auto-logged in, redirected to catalog")
    void register_validData_redirectsToCatalog() {
        String name  = fakerUtils.randomName();
        String email = fakerUtils.randomEmail();
        String pass  = fakerUtils.randomPassword();

        CatalogPage catalog = new RegisterPage().open()
                .registerAs(name, email, pass);

        catalog.waitForProducts();
        catalog.navbar().assertLoggedIn();
        catalog.navbar().assertUserName(name);

        dbAssertions.assertUserExists(email);
        dbAssertions.assertUserRole(email, "USER");
    }

    @Test
    @Story("Registration validation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Register with duplicate email → error message shown")
    void register_duplicateEmail_showsError() {
        String name  = fakerUtils.randomName();
        String email = fakerUtils.randomEmail();
        String pass  = fakerUtils.randomPassword();

        // First registration succeeds
        new RegisterPage().open().registerAs(name, email, pass)
                .waitForProducts()
                .navbar().logout();

        // Second registration with same email fails
        new RegisterPage().open()
                .enterName(fakerUtils.randomName())
                .enterEmail(email)
                .enterPassword(pass)
                .clickRegisterExpectFailure();
    }

    @Test
    @Story("Registration validation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Register with short password → HTML5 validation or server error")
    void register_shortPassword_isRejected() {
        new RegisterPage().open()
                .enterName(fakerUtils.randomName())
                .enterEmail(fakerUtils.randomEmail())
                .enterPassword("abc")     // too short (< 6 chars)
                .clickRegisterExpectFailure();
    }

    @Test
    @Story("Navigation")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Register page has link back to login")
    void registerPage_hasLoginLink() {
        new RegisterPage().open().goToLogin();
    }
}
