package com.app.chat.auth.service;

import com.app.chat.auth.dto.AuthResponse;
import com.app.chat.auth.dto.LoginRequest;
import com.app.chat.auth.dto.RegisterRequest;
import com.app.chat.auth.model.User;
import com.app.chat.auth.repository.UserRepository;
import com.app.chat.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

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

        if(passwordEncoder.matches(request.password(), user.getPassword())) {
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

}