package com.app.chat.auth.repository;

import com.app.chat.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserName(String name);
    Optional<User> findByEmail(String email);

    boolean existsByUserName(String name);
    boolean existsByEmail(String email);
}
