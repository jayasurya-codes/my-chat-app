package com.app.chat.auth.dto;

public record RegisterRequest(
        String userName,
        String email,
        String password
) {}
