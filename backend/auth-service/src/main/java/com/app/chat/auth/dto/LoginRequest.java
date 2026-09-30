package com.app.chat.auth.dto;

public record LoginRequest(
        String userName,
        String password
) {
}
