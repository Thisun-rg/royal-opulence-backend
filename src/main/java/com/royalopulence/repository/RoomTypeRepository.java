package com.royalopulence.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.royalopulence.model.core.RoomType;

public interface RoomTypeRepository extends MongoRepository<RoomType, String> {
    Optional<RoomType> findByCodeIgnoreCase(String code);
}
