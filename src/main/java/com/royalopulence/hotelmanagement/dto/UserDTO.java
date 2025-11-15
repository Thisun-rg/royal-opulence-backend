package com.royalopulence.hotelmanagement.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String fullName;
    private String email;
    private String role;
}