package com.app.chat.auth.controller;

import com.app.chat.auth.dto.AuthResponse;
import com.app.chat.auth.dto.LoginRequest;
import com.app.chat.auth.dto.RegisterRequest;
import com.app.chat.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authservice;

    public AuthController(AuthService authservice) {
        this.authservice = authservice;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(RegisterRequest request) {
        AuthResponse response = authservice.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        AuthResponse response = authservice.login(request);
        return ResponseEntity.ok(response);
    }



}
