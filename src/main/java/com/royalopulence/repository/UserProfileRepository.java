package com.royalopulence.repository;

import com.royalopulence.model.core.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    // Fetch profile by linked userId (User._id)
    Optional<UserProfile> findByUserId(String userId);
}
