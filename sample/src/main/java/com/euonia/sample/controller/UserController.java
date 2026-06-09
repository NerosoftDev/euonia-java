package com.euonia.sample.controller;

import com.euonia.factory.ObjectFactory;
import com.euonia.sample.domain.aggregate.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequestScope
public class UserController {
    private final ObjectFactory factory;

    public UserController(ObjectFactory factory) {
        this.factory = factory;
    }

    @PostMapping()
    public ResponseEntity<String> createUser(@RequestBody Map<String, Object> params) {
        var user = factory.create(User.class, params.getOrDefault("name", "Default Name"));
        try (user) {
            user.onSaved((args) -> {
                System.out.println("User saved: " + ((User) args.getNewObject()).getEvents().size());
            });
            user.setAge(20);
            user.save(false);
            return ResponseEntity.ok("User created with name: " + user.getName());
        }

    }

    @GetMapping("{id}")
    public ResponseEntity<String> getUser(@PathVariable long id) {
        var user = factory.fetch(User.class, id);
        try (user) {
            return ResponseEntity.ok("User fetched with name: " + user.getName());
        }
    }
}
