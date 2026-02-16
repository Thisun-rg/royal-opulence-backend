package com.royalopulence.service.impl;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.royalopulence.dto.auth.AuthResponse;
import com.royalopulence.dto.auth.LoginRequest;
import com.royalopulence.dto.auth.RegisterRequest;
import com.royalopulence.model.core.Role;
import com.royalopulence.model.core.User;
import com.royalopulence.repository.RoleRepository;
import com.royalopulence.repository.UserRepository;
import com.royalopulence.service.base.UserService;
import com.royalopulence.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // REGISTER
    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        // Ensure GUEST role exists
        Role defaultRole = roleRepository.findByName("GUEST")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("GUEST");
                    return roleRepository.save(role);
                });

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Store role name (Set<String>)
        user.setRoles(Collections.singleton(defaultRole.getName()));

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail());
        return new AuthResponse(token, savedUser.getEmail(), "GUEST");
    }

    // LOGIN
    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        String role = user.getRoles().stream().findFirst().orElse("GUEST");

        return new AuthResponse(token, user.getEmail(), role);
    }
}
