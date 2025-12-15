package com.royalopulence.repository;

import com.royalopulence.model.core.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PromotionRepository extends MongoRepository<Promotion, String> {

    // ✅ ADD THIS METHOD
    List<Promotion> findByUserId(String userId);
}
