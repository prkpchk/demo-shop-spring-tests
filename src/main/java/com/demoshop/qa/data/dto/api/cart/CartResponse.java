package com.demoshop.qa.data.dto.api.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(Long id, List<CartItemResponse> items, BigDecimal total) {
}
