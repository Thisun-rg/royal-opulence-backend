package com.royalopulence.controller;

import com.royalopulence.dto.user.UpdateUserRequest;
import com.royalopulence.dto.user.UserResponse;
import com.royalopulence.service.base.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getProfile() {
        return userService.getCurrentUserProfile();
    }

    @PutMapping("/me")
    public UserResponse updateProfile(@RequestBody UpdateUserRequest request) {
        return userService.updateCurrentUserProfile(request);
    }
}
