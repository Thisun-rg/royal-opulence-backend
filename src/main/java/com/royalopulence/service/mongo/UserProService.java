package com.royalopulence.service.mongo;

import com.royalopulence.model.mongo.UserPro;
import com.royalopulence.repository.mongo.UserProRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProService {

    private final UserProRepository userProRepository;

    public UserProService(UserProRepository userProRepository) {
        this.userProRepository = userProRepository;
    }

    public UserPro saveUser(UserPro user) {
        return userProRepository.save(user);
    }

    public List<UserPro> getAllUsers() {
        return userProRepository.findAll();
    }
}
