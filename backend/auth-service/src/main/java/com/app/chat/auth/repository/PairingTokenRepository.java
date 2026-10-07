package com.app.chat.auth.repository;

import com.app.chat.auth.model.PairingToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface PairingTokenRepository extends JpaRepository<PairingToken, UUID> {

    Optional<PairingToken> findByTokenCode(String token);


}
