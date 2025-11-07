package com.royalopulence.service.impl;

import com.royalopulence.dto.auth.*;
import com.royalopulence.model.core.*;
import com.royalopulence.repository.*;
import com.royalopulence.service.base.UserService;
import com.royalopulence.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        System.out.println("📥 Register request for: " + request.getEmail());

        // ✅ Make sure the user doesn’t already exist
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            System.out.println("⚠️ Email already registered: " + request.getEmail());
            throw new RuntimeException("Email already exists!");
        }

        // ✅ Ensure a 'GUEST' role exists
        Role defaultRole = roleRepository.findByName("GUEST")
                .orElseGet(() -> {
                    System.out.println("🆕 Creating new role: GUEST");
                    return roleRepository.save(new Role(null, "GUEST"));
                });

        // ✅ Create the new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton(defaultRole));

        // ✅ Save to DB
        User savedUser = userRepository.save(user);
        System.out.println("✅ User saved successfully: ID = " + savedUser.getId());

        // ✅ Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail());
        return new AuthResponse(token, savedUser.getEmail(), "GUEST");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        String role = user.getRoles().iterator().next().getName();

        return new AuthResponse(token, user.getEmail(), role);
    }
}
