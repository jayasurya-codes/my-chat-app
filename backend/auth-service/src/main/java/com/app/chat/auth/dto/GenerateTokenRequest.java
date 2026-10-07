package com.app.chat.auth.dto;

import com.app.chat.auth.model.PairingToken.TokenType;
public record GenerateTokenRequest(
        TokenType type,
        int maxUses,
        int validityMinutes
) { }
