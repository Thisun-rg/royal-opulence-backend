package com.royalopulence.repository;

import com.royalopulence.model.core.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}

<<<<<<< HEAD


=======
>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3
