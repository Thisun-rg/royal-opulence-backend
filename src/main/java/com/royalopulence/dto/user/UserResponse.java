package com.royalopulence.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String email;
    private String name;
    private String phone;
    private String address;
}
