package com.demoshop.qa.assertions;

import com.demoshop.qa.clients.database.DatabaseHelper;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * High-level assertion helpers that combine DatabaseHelper queries with AssertJ assertions.
 */
@Component
public class DbAssertions {

    @Autowired
    private DatabaseHelper db;

    @Step("DB: assert user '{email}' exists")
    public Map<String, Object> assertUserExists(String email) {
        Optional<Map<String, Object>> user = db.findUserByEmail(email);
        assertThat(user).as("User with email '%s' should exist in DB", email).isPresent();
        return user.get();
    }

    @Step("DB: assert user '{email}' does NOT exist")
    public void assertUserNotExists(String email) {
        Optional<Map<String, Object>> user = db.findUserByEmail(email);
        assertThat(user).as("User with email '%s' should NOT exist in DB", email).isEmpty();
    }

    @Step("DB: assert user '{email}' has role '{expectedRole}'")
    public void assertUserRole(String email, String expectedRole) {
        Map<String, Object> user = assertUserExists(email);
        assertThat(user.get("role").toString())
                .as("User role").isEqualTo(expectedRole);
    }

    @Step("DB: assert user '{email}' balance is at least {minBalance}")
    public void assertUserBalanceAtLeast(String email, BigDecimal minBalance) {
        BigDecimal balance = db.getUserBalance(email);
        assertThat(balance)
                .as("Balance for user '%s'", email)
                .isGreaterThanOrEqualTo(minBalance);
    }

    @Step("DB: assert order {orderId} has status '{expectedStatus}'")
    public void assertOrderStatus(Long orderId, String expectedStatus) {
        String status = db.getOrderStatus(orderId);
        assertThat(status)
                .as("Order %d status", orderId)
                .isEqualTo(expectedStatus);
    }

    @Step("DB: assert user '{email}' has at least {minOrders} orders")
    public void assertUserHasOrders(String email, int minOrders) {
        int count = db.findOrdersByUserEmail(email).size();
        assertThat(count)
                .as("Order count for '%s'", email)
                .isGreaterThanOrEqualTo(minOrders);
    }

    @Step("DB: assert product {productId} stock is {expectedStock}")
    public void assertProductStock(Long productId, int expectedStock) {
        int stock = db.getProductStock(productId);
        assertThat(stock)
                .as("Stock for product %d", productId)
                .isEqualTo(expectedStock);
    }

    @Step("DB: assert cart for user '{email}' has {expectedItems} items")
    public void assertCartItemCount(String email, int expectedItems) {
        int count = db.countCartItemsByUserEmail(email);
        assertThat(count)
                .as("Cart item count for '%s'", email)
                .isEqualTo(expectedItems);
    }
}
