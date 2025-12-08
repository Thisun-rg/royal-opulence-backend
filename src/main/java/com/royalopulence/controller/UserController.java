package com.royalopulence.controller;

import com.royalopulence.dto.user.UpdateUserRequest;
import com.royalopulence.dto.user.UserResponse;
import com.royalopulence.service.base.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateMyProfile(request));
    }
}
