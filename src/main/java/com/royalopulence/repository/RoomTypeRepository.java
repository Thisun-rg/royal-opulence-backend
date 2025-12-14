package com.royalopulence.repository;

import com.royalopulence.model.core.RoomType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomTypeRepository extends MongoRepository<RoomType, String> {
}
