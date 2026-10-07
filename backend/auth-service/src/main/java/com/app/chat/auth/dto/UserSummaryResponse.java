package com.app.chat.auth.dto;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String userName,
        String avatarUrl
) {}
