package com.demoshop.qa.core.junit.extensions;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JUnit 5 extension that retries a failing test up to MAX_RETRIES times.
 * Each retry re-invokes the test method on the existing test instance.
 * Note: @BeforeEach/@AfterEach callbacks are NOT repeated for retries — use
 * self-contained test methods (idempotent setup) when relying on this extension.
 */
public class RetryExtension implements TestExecutionExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RetryExtension.class);
    private static final int MAX_RETRIES = 3;
    private static final ExtensionContext.Namespace NS =
            ExtensionContext.Namespace.create(RetryExtension.class);

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        int retryCount = context.getStore(NS)
                .getOrComputeIfAbsent("retryCount", k -> 0, Integer.class);

        if (retryCount < MAX_RETRIES) {
            int attempt = retryCount + 1;
            context.getStore(NS).put("retryCount", attempt);

            log.warn("Test '{}' failed on attempt {}/{}. Retrying...",
                    context.getDisplayName(), attempt, MAX_RETRIES);

            Allure.step(String.format("⚠ Retry attempt %d/%d", attempt, MAX_RETRIES));

            Method method = context.getRequiredTestMethod();
            Object instance = context.getRequiredTestInstance();
            method.setAccessible(true);

            try {
                method.invoke(instance);
                // Retry succeeded — reset counter and return normally
                context.getStore(NS).put("retryCount", 0);
            } catch (InvocationTargetException e) {
                handleTestExecutionException(context, e.getCause());
            } catch (IllegalAccessException e) {
                throw throwable;
            }
        } else {
            context.getStore(NS).put("retryCount", 0);
            log.error("Test '{}' failed after {} retries.", context.getDisplayName(), MAX_RETRIES);
            throw throwable;
        }
    }
}
