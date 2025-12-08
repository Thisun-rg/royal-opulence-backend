package com.royalopulence.service.impl;

import com.royalopulence.service.base.UserService;


import com.royalopulence.dto.auth.AuthResponse;
import com.royalopulence.dto.auth.LoginRequest;
import com.royalopulence.dto.auth.RegisterRequest;
import com.royalopulence.model.core.User;
import com.royalopulence.repository.UserRepository;
import com.royalopulence.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER"); // default role
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Return token, email, and role
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Return token, email, and role
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }
}
