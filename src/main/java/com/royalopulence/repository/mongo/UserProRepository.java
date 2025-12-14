package com.royalopulence.repository.mongo;

import com.royalopulence.model.mongo.UserPro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProRepository extends MongoRepository<UserPro, String> {
}
