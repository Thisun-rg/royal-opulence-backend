package com.royalopulence.mapper;

import com.royalopulence.dto.user.UpdateUserRequest;
import com.royalopulence.dto.user.UserResponse;
import com.royalopulence.model.core.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Convert Entity → Response DTO
     */
    public UserResponse toResponse(UserProfile profile) {
        UserResponse res = new UserResponse();

        res.setId(profile.getId());
        res.setFirstName(profile.getFirstName());
        res.setLastName(profile.getLastName());
        res.setPhone(profile.getPhone());
        res.setCountry(profile.getCountry());
        res.setCity(profile.getCity());
        res.setAddress(profile.getAddress());
        res.setAvatarUrl(profile.getAvatarUrl());
        res.setPreferredLanguage(profile.getPreferredLanguage());
        res.setPreferredCurrency(profile.getPreferredCurrency());
        res.setMarketingOptIn(profile.isMarketingOptIn());
        res.setLoyaltyLevel(profile.getLoyaltyLevel());
        res.setLoyaltyPoints(profile.getLoyaltyPoints());
        res.setEmergencyContactName(profile.getEmergencyContactName());
        res.setEmergencyContactPhone(profile.getEmergencyContactPhone());
        res.setNationality(profile.getNationality());
        res.setPassportNumber(profile.getPassportNumber());

        return res;
    }

    /**
     * Apply UpdateUserRequest → Entity
     */
    public void updateEntity(UserProfile profile, UpdateUserRequest request) {

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setCountry(request.getCountry());
        profile.setCity(request.getCity());
        profile.setAddress(request.getAddress());
        profile.setAvatarUrl(request.getAvatarUrl());

        profile.setPreferredLanguage(request.getPreferredLanguage());
        profile.setPreferredCurrency(request.getPreferredCurrency());
        profile.setMarketingOptIn(request.isMarketingOptIn());

        profile.setEmergencyContactName(request.getEmergencyContactName());
        profile.setEmergencyContactPhone(request.getEmergencyContactPhone());

        profile.setNationality(request.getNationality());
        profile.setPassportNumber(request.getPassportNumber());
    }
}
