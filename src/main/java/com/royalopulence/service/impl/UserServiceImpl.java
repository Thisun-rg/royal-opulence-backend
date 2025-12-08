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
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new RuntimeException("Email already exists!");
    }

    // ensure role exists in roles collection
    Role defaultRole = roleRepository.findByName("GUEST")
            .orElseGet(() -> roleRepository.save(new Role(null, "GUEST")));

    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    // store only the role name to simplify reads
    user.setRoles(Collections.singleton(defaultRole.getName()));

    User saved = userRepository.save(user);
    String token = jwtUtil.generateToken(saved.getEmail());
    return new AuthResponse(token, saved.getEmail(), "GUEST");
}


    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

       User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));

    String token = jwtUtil.generateToken(user.getEmail());
    String role = user.getRoles().stream().findFirst().orElse("GUEST");

    return new AuthResponse(token, user.getEmail(), role);

    }
}

//pretier