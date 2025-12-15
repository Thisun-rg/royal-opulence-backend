package com.royalopulence.repository;

import com.royalopulence.model.core.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends MongoRepository<Promotion, String> {

    // user-specific + general promotions
    List<Promotion> findByTargetUserIdOrTargetUserIdIsNull(String userId);
}
