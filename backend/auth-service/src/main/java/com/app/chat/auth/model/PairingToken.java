package com.app.chat.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "pairing_tokens",
        indexes = {
                @Index(name = "idx_pairing_token_code", columnList = "token_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PairingToken {

    public enum TokenType { QR, PIN }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "token_id", updatable = false, nullable = false)
    private UUID tokenId;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    @Column(name = "token_code", nullable = false, unique = true, length = 36)
    private String tokenCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TokenType type;

    @Column(name = "max_uses", nullable = false)
    private int maxUses;

    @Column(name = "current_uses", nullable = false)
    private int currentUses = 0;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public PairingToken(UUID creatorId, String tokenCode, TokenType type, int maxUses, Instant expiresAt) {
        this.creatorId = creatorId;
        this.tokenCode = tokenCode;
        this.type = type;
        this.maxUses = maxUses;
        this.expiresAt = expiresAt;
    }

    public boolean isValid() {
        return currentUses < maxUses && Instant.now().isBefore(expiresAt);
    }
}