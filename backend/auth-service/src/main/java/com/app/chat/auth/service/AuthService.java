package com.app.chat.auth.service;

import com.app.chat.auth.dto.*;
import com.app.chat.auth.model.FriendRequest;
import com.app.chat.auth.model.PairingToken;
import com.app.chat.auth.model.User;
import com.app.chat.auth.repository.ContactRepository;
import com.app.chat.auth.repository.FriendRequestRepository;
import com.app.chat.auth.repository.PairingTokenRepository;
import com.app.chat.auth.repository.UserRepository;
import com.app.chat.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final PairingTokenRepository pairingTokenRepository;
    private final ContactRepository contactRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUserName(request.userName())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User(
                request.userName(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getUserName());

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getUserName(),
                savedUser.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUserName(request.userName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid  username or password"));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = tokenProvider.generateToken(user.getId(), user.getUserName());
        return new AuthResponse(
                token,
                user.getId(),
                user.getUserName(),
                user.getEmail()
        );
    }

    @Transactional
    public String generatePairingToken(UUID creatorId, GenerateTokenRequest request) {
        String tokenCode;

        if(request.type() == PairingToken.TokenType.PIN) {
            tokenCode = String.format("%06d", new java.security.SecureRandom().nextInt(1000000));
        } else {
            tokenCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }
        Instant expiresAt = Instant.now().plus(request.validityMinutes(), java.time.temporal.ChronoUnit.MINUTES);
        PairingToken token = new PairingToken(creatorId, tokenCode, request.type(), request.maxUses(), expiresAt);
        pairingTokenRepository.save(token);

        return tokenCode;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileByUserName(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: "+userName));

        return new UserProfileResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getCreatedAt()

        );
    }

    @Transactional
    public UserProfileResponse updateProfileByID(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found  "));

        if(request.userName() != null && !request.userName().isBlank()) {
            String newUserName = request.userName().trim();
            if(!user.getUserName().equalsIgnoreCase(newUserName) && userRepository.existsByUserName(newUserName)) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUserName(newUserName);
        }

        if(request.email() != null && !request.email().isBlank()) {
            String newEmail = request.email().trim();
            if(!user.getEmail().equalsIgnoreCase(newEmail) && userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email is already taken");
            }
            user.setEmail(newEmail);
        }

        if(request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl().trim());
        }

        User updatedUser = userRepository.save(user);

        return new UserProfileResponse(
                updatedUser.getId(),
                updatedUser.getUserName(),
                updatedUser.getEmail(),
                updatedUser.getAvatarUrl(),
                updatedUser.getCreatedAt()

        );

    }

    @Transactional
    public void deleteProfileById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        userRepository.delete(user);
    }

    @Transactional
    public void ConsumeTokenAndSendRequest(UUID senderId, String tokenCode) {
        PairingToken token = pairingTokenRepository.findByTokenCode(tokenCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired pairing code"));

        if(!token.isValid()) {
            throw new IllegalArgumentException("Invalid or expired pairing code");
        }
        if(token.getCreatorId().equals(senderId)) {
            throw new IllegalArgumentException("Cannot send a friend request to yourself");
        }

        if(contactRepository.existsByUserIdAndContactId(senderId, token.getCreatorId())) {
            throw new IllegalArgumentException("You are already friends");
        }

        friendRequestRepository.findExistingRequestBetween(senderId, token.getCreatorId())
                        .ifPresent(existing -> {
                            if(existing.getStatus() == FriendRequest.RequestStatus.PENDING) {
                                throw new IllegalArgumentException("A friend request is already pending between users");
                            }
                        });

        token.setCurrentUses(token.getCurrentUses() +1);
        pairingTokenRepository.save(token);

        FriendRequest friendRequest = new FriendRequest(senderId, token.getCreatorId());
        friendRequestRepository.save(friendRequest);
    }

}