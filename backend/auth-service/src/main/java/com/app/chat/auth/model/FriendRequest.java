package com.app.chat.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "friend_requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_sender_receiver", columnNames = {"sender_id", "receiver_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class FriendRequest {

    public enum RequestStatus {PENDING, ACCEPTED, REJECTED}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "equest_id", updatable = false, nullable = false)
    private UUID requestId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name= "reciver_id", nullable = false)
    private UUID reciverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public FriendRequest(UUID senderId, UUID reciverId) {
        this.senderId = senderId;
        this.reciverId = reciverId;
    }
}
