package com.demoshop.qa.data.dto.api.user;

import java.math.BigDecimal;

public record UserResponse(Long id, String email, String name, String role, BigDecimal balance) {
}
