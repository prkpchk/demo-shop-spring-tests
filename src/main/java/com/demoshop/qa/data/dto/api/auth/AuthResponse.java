package com.demoshop.qa.data.dto.api.auth;

public record AuthResponse(String token, String email, String name, String role) {
}
