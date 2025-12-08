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

    // reference to main user (User._id)
    private String userId;

    // Basic info
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    private String city;
    private String address;

    // Profile preferences
    private String avatarUrl;            // stored in S3 or local storage
    private String preferredLanguage;    // EN, SI, TA...
    private String preferredCurrency;    // LKR, USD, EUR...
    private boolean marketingOptIn;

    // Loyalty & membership
    private String loyaltyLevel;         // Basic, Silver, Gold, Platinum
    private int loyaltyPoints;

    // Emergency contact
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Additional identity fields
    private String nationality;
    private String passportNumber;
}
