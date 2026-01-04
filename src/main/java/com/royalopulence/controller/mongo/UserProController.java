package com.royalopulence.controller.mongo;

import com.royalopulence.model.mongo.UserPro;
import com.royalopulence.service.mongo.UserProService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userpro")
public class UserProController {

    private final UserProService userProService;

    public UserProController(UserProService userProService) {
        this.userProService = userProService;
    }

    @PostMapping
    public UserPro saveUser(@RequestBody UserPro user) {
        return userProService.saveUser(user);
    }

    @GetMapping
    public List<UserPro> getUsers() {
        return userProService.getAllUsers();
    }
}
