package com.demoshop.qa.data.dto.api.product;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        String description,
        BigDecimal price,
        int stock,
        String category,
        String imageUrl
) {
}
