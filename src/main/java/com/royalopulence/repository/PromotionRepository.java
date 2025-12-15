package com.royalopulence.repository;

import com.royalopulence.model.core.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends MongoRepository<Promotion, String> {
}
