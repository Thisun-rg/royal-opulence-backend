package com.royalopulence.controller;

import com.royalopulence.model.core.Role;
import com.royalopulence.model.core.User;
import com.royalopulence.repository.RoleRepository;
import com.royalopulence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class AdminSetupController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create-admin")
    public String createAdmin() {

        // جلوگیری duplicate admin
        if (userRepository.findByEmail("admin@royalopulence.com").isPresent()) {
            return "Admin user already exists.";
        }

        // Get or create ADMIN role
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });

        // Create admin user
        User admin = new User();
        admin.setName("Super Admin");
        admin.setEmail("admin@royalopulence.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));

        // If roles stored as Set<String>
        admin.setRoles(Collections.singleton(adminRole.getName()));

        userRepository.save(admin);

        return "Admin user created successfully!";
    }
}
