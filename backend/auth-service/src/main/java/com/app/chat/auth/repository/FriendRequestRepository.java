package com.app.chat.auth.repository;

import com.app.chat.auth.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {

    @Query("""
        SELECT fr FROM FriendRequest fr\s
                WHERE (fr.senderId = :userA AND fr.receiverId = :userB)\s
                   OR (fr.senderId = :userB AND fr.receiverId = :userA)
""")
    Optional<FriendRequest> findExistingRequestBetween(@Param("userA") UUID userA, @Param("userB") UUID userG);
}
