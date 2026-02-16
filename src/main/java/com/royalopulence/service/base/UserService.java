package com.royalopulence.service.base;

import com.royalopulence.dto.auth.*;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
