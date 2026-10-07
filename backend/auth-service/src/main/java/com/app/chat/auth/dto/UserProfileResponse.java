package com.app.chat.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String userName,
        String email,
        String avatarUrl,
        Instant createdAt
) { }
