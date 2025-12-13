package com.royalopulence.service.base;

import com.royalopulence.dto.auth.AuthResponse;
import com.royalopulence.dto.auth.LoginRequest;
import com.royalopulence.dto.auth.RegisterRequest;
import com.royalopulence.dto.user.UpdateUserRequest;
import com.royalopulence.dto.user.UserResponse;

public interface UserService {

    // AUTH
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);

    // USER PROFILE
    UserResponse getCurrentUserProfile();
    UserResponse updateCurrentUserProfile(UpdateUserRequest request);
}
