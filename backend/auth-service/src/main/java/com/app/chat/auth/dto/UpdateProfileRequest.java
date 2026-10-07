package com.app.chat.auth.dto;

public record UpdateProfileRequest(
        String userName,
        String email,
        String avatarUrl
) { }
