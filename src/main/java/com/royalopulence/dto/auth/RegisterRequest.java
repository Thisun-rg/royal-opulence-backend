package com.royalopulence.dto.auth;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank private String name;
    @Email private String email;
    @NotBlank private String password;
}
