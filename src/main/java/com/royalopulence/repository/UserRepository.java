package com.royalopulence.repository;

import com.royalopulence.model.core.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
<<<<<<< HEAD

=======
>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

