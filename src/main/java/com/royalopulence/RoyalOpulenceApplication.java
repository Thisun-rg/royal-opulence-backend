package com.royalopulence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class RoyalOpulenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoyalOpulenceApplication.class, args);
    }
}
