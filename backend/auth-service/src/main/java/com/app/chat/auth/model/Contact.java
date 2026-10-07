package com.app.chat.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "contacts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_contact", columnNames = {"user_id", "contact_id"})
        },
        indexes = {
                @Index(name = "idx_contacts_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "contact_id", nullable = false, updatable = false)
    private UUID contactId;

    @Column(name = "alias")
    private String alias;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Contact(UUID usedID, UUID contactId) {
        this.userId = userId;
        this.contactId = contactId;
    }

    public Contact(UUID userId, UUID contactId, String alias) {
        this.userId = userId;
        this.contactId = contactId;
        this.alias = alias;
    }
}
