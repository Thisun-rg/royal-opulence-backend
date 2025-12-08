package com.royalopulence.model.core;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user_profiles")
public class UserProfile {

    @Id
    private String id;

    private String userId;          // Reference to User._id

    // Basic info
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    private String city;
    private String address;

    // Profile preferences
    private String avatarUrl;       // Profile image
    private String preferredLanguage;
    private String preferredCurrency;
    private boolean marketingOptIn;

    // Loyalty & membership
    private String loyaltyLevel;    // Basic, Silver, Gold, Platinum
    private int loyaltyPoints;

    // Emergency details
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Additional fields
    private String nationality;
    private String passportNumber;
}
