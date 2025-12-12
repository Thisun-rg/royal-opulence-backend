package com.royalopulence.dto.user;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    private String city;
    private String address;
    private String avatarUrl;
    private String preferredLanguage;
    private String preferredCurrency;
    private boolean marketingOptIn;
    private String loyaltyLevel;
    private int loyaltyPoints;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String nationality;
    private String passportNumber;
}
