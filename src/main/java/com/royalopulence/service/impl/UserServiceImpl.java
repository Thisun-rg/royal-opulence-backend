package com.royalopulence.service.impl;

import com.royalopulence.dto.auth.AuthResponse;
import com.royalopulence.dto.auth.LoginRequest;
import com.royalopulence.dto.auth.RegisterRequest;
import com.royalopulence.dto.user.UpdateUserRequest;
import com.royalopulence.dto.user.UserResponse;
import com.royalopulence.mapper.UserMapper;
import com.royalopulence.model.core.User;
import com.royalopulence.model.core.UserProfile;
import com.royalopulence.repository.UserProfileRepository;
import com.royalopulence.repository.UserRepository;
import com.royalopulence.service.base.UserService;
import com.royalopulence.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ================= AUTH =================

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of("USER"));

        userRepository.save(user);

        // create empty profile
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setName(request.getName());
        userProfileRepository.save(profile);

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getEmail(), "USER");
    }

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

        String role = user.getRoles().iterator().next();
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getEmail(), role);
    }

    // ================= PROFILE =================

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        UserProfile profile = user.getProfile();

        return UserMapper.toResponse(user, profile);
    }

    @Override
    public UserResponse updateCurrentUserProfile(UpdateUserRequest request) {
        User user = getCurrentUser();
        UserProfile profile = user.getProfile();

        profile.setName(request.getName());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());

        userProfileRepository.save(profile);

        return UserMapper.toResponse(user, profile);
    }
}
