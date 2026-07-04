package com.demoshop.qa.clients.database;

import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Low-level JDBC helper for test data inspection and direct DB queries.
 * All write operations should go through the application API; reads here are for
 * post-action verification only.
 */
@Component
public class DatabaseHelper {

    private static final Logger log = LoggerFactory.getLogger(DatabaseHelper.class);

    @Autowired
    private JdbcTemplate jdbc;

    // ─── Users ────────────────────────────────────────────────────────────────

    @Step("DB: find user by email '{email}'")
    public Optional<Map<String, Object>> findUserByEmail(String email) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT * FROM users WHERE email = ?", email);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    @Step("DB: get user balance for email '{email}'")
    public BigDecimal getUserBalance(String email) {
        return jdbc.queryForObject(
                "SELECT balance FROM users WHERE email = ?",
                BigDecimal.class, email);
    }

    // ─── Orders ───────────────────────────────────────────────────────────────

    @Step("DB: find orders for user email '{email}'")
    public List<Map<String, Object>> findOrdersByUserEmail(String email) {
        return jdbc.queryForList(
                "SELECT o.* FROM orders o JOIN users u ON o.user_id = u.id WHERE u.email = ?",
                email);
    }

    @Step("DB: get order status by order id {orderId}")
    public String getOrderStatus(Long orderId) {
        return jdbc.queryForObject(
                "SELECT status FROM orders WHERE id = ?",
                String.class, orderId);
    }

    @Step("DB: count order items for order {orderId}")
    public int countOrderItems(Long orderId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM order_items WHERE order_id = ?",
                Integer.class, orderId);
        return count != null ? count : 0;
    }

    // ─── Products ─────────────────────────────────────────────────────────────

    @Step("DB: find product by id {productId}")
    public Optional<Map<String, Object>> findProductById(Long productId) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT * FROM products WHERE id = ?", productId);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    @Step("DB: get product stock for product id {productId}")
    public int getProductStock(Long productId) {
        Integer stock = jdbc.queryForObject(
                "SELECT stock FROM products WHERE id = ?",
                Integer.class, productId);
        return stock != null ? stock : 0;
    }

    // ─── Cart ─────────────────────────────────────────────────────────────────

    @Step("DB: count cart items for user email '{email}'")
    public int countCartItemsByUserEmail(String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM cart_items ci "
                + "JOIN carts c ON ci.cart_id = c.id "
                + "JOIN users u ON c.user_id = u.id "
                + "WHERE u.email = ?",
                Integer.class, email);
        return count != null ? count : 0;
    }

    // ─── Generic ──────────────────────────────────────────────────────────────

    public List<Map<String, Object>> query(String sql, Object... args) {
        log.debug("DB query: {}", sql);
        return jdbc.queryForList(sql, args);
    }

    public int count(String table, String where, Object... args) {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + where;
        Integer count = jdbc.queryForObject(sql, Integer.class, args);
        return count != null ? count : 0;
    }
}
