package com.royalopulence.hotelmanagement.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDTO {
    private String fullName;
    private String email;
    private String password;
    private String role;
}