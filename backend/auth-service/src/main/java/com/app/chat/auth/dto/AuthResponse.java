package com.app.chat.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId,
        String userName,
        String email
) {
}
