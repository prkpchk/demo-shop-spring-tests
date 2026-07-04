package com.demoshop.qa.dataproviders;

import com.demoshop.qa.data.dto.api.auth.RegisterRequest;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FakerUtils {

    private final Faker faker = new Faker();

    public String randomEmail() {
        String localPart = faker.name().username()
                .replaceAll("[^a-zA-Z0-9._-]", "");   // keep only chars valid in the local part
        return localPart
                + "." + System.nanoTime()             // guarantee uniqueness
                + "@test.com";
    }

    public String randomName() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    public String randomPassword() {
        // At least 6 chars (app requirement)
        return faker.internet().password(8, 16, true, true);
    }

    public RegisterRequest randomRegisterRequest() {
        return new RegisterRequest(randomName(), randomEmail(), randomPassword());
    }

    public String randomProductName() {
        return faker.commerce().productName() + " " + System.nanoTime();
    }

    public String randomProductDescription() {
        return faker.lorem().sentence();
    }

    public BigDecimal randomPrice() {
        return BigDecimal.valueOf(faker.number().randomDouble(2, 1, 999));
    }

    public int randomStock() {
        return faker.number().numberBetween(10, 100);
    }

    public String randomCategory() {
        String[] categories = {"Electronics", "Home", "Stationery", "Accessories", "Bags"};
        return categories[faker.random().nextInt(categories.length)];
    }

    public BigDecimal randomTopUpAmount() {
        return BigDecimal.valueOf(faker.number().randomDouble(2, 10, 500));
    }
}
