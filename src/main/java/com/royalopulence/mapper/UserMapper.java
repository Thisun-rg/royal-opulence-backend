package com.royalopulence.mapper;

import com.royalopulence.dto.user.UserResponse;
import com.royalopulence.model.core.User;
import com.royalopulence.model.core.UserProfile;

public class UserMapper {

    public static UserResponse toResponse(User user, UserProfile profile) {
        return UserResponse.builder()
                .email(user.getEmail())
                .name(profile.getName())
                .phone(profile.getPhone())
                .address(profile.getAddress())
                .build();
    }
}
