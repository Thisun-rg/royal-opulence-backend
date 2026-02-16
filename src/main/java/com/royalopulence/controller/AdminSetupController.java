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
    if (userRepository.findByEmail("admin@royalopulence.com").isPresent()) {
        return "Admin user already exists.";
    }

    Role adminRole = roleRepository.findByName("ADMIN")
        .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));

    User admin = new User();
    admin.setName("Super Admin");
    admin.setEmail("admin@royalopulence.com");
    admin.setPassword(passwordEncoder.encode("Admin@123"));
    // store role *name*
    admin.setRoles(Collections.singleton(adminRole.getName()));
    userRepository.save(admin);

    return "Admin user created successfully!";
}

}
