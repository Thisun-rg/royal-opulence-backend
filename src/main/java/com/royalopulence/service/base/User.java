package com.royalopulence.service.base;

import com.royalopulence.dto.user.UpdateUserRequest;
import com.royalopulence.dto.user.UserResponse;

public interface User {
    UserResponse getCurrentUserProfile();
    UserResponse updateCurrentUserProfile(UpdateUserRequest request);
}
