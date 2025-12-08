package com.royalopulence.service.base;

import com.royalopulence.dto.auth.AuthResponse;
import com.royalopulence.dto.auth.LoginRequest;
import com.royalopulence.dto.auth.RegisterRequest;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
