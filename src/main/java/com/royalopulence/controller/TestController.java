package com.royalopulence.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public String hello(@AuthenticationPrincipal UserDetails userDetails) {
        return "✅ Hello " + userDetails.getUsername() + ", your token is valid!";
    }
}
