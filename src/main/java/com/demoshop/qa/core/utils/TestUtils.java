package com.demoshop.qa.core.utils;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.BooleanSupplier;

@Component
public class TestUtils {

    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    /**
     * Polls {@code condition} every {@code pollIntervalMs} ms until it returns {@code true}
     * or {@code timeoutMs} elapses. Returns whether the condition became true.
     */
    public boolean waitUntil(BooleanSupplier condition, long timeoutMs, long pollIntervalMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            try {
                Thread.sleep(pollIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return condition.getAsBoolean();
    }

    /** Attach arbitrary text to the current Allure report. */
    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }

    /** Attach JSON string to the current Allure report. */
    public static void attachJson(String name, String json) {
        Allure.addAttachment(name, "application/json", json);
    }
}
