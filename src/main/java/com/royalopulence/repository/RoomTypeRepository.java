package com.royalopulence.repository;

<<<<<<< HEAD
import com.royalopulence.model.core.RoomType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomTypeRepository extends MongoRepository<RoomType, String> {
=======
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.royalopulence.model.core.RoomType;

public interface RoomTypeRepository extends MongoRepository<RoomType, String> {
    Optional<RoomType> findByCodeIgnoreCase(String code);
>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3
}
