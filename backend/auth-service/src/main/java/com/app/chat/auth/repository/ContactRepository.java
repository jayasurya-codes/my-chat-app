package com.app.chat.auth.repository;

import com.app.chat.auth.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {

    boolean existsByUserIdAndContactId(UUID userId, UUID contactId);
}
