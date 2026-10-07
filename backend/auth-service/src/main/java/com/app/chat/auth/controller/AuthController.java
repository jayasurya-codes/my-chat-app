package com.app.chat.auth.controller;

import com.app.chat.auth.dto.*;
import com.app.chat.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/users")
public class AuthController {

    private final AuthService authservice;

    public AuthController(AuthService authservice) {
        this.authservice = authservice;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authservice.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authservice.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile/generate")
    public ResponseEntity<String> generatePairingToken(@AuthenticationPrincipal String authenticatedUserId, @RequestBody GenerateTokenRequest request) {
        UUID userId = UUID.fromString(authenticatedUserId);
        String code = authservice.generatePairingToken(userId, request);
        return ResponseEntity.ok(code);
    }

    @PostMapping("/profile/consume")
    public ResponseEntity<Void> ConsumePairingToken(@AuthenticationPrincipal String authenticatedUserId,
                                                    @RequestBody ConsumeTokenRequest request) {
        UUID userId = UUID.fromString(authenticatedUserId);
        authservice.ConsumeTokenAndSendRequest(userId, request.tokenCode());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable("username") String username) {
        return ResponseEntity.ok(authservice.getUserProfileByUserName(username));

    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal String authenticatedUserId,
            @RequestBody UpdateProfileRequest request
    ) {
        UUID userId = UUID.fromString(authenticatedUserId);
        return ResponseEntity.ok(authservice.updateProfileByID(userId, request));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal String authenticatedUserId) {
        UUID userId = UUID.fromString(authenticatedUserId);
        authservice.deleteProfileById(userId);
        return ResponseEntity.noContent().build();

    }



}
