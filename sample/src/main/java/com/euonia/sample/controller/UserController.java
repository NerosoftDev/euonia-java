package com.euonia.sample.controller;

import com.euonia.sample.application.contract.UserApplicationService;
import com.euonia.sample.application.dto.UserCreateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

@RestController
@RequestMapping("/api/user")
@RequestScope
public class UserController {
    private final UserApplicationService service;

    public UserController(UserApplicationService service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<String> createUser(@RequestBody UserCreateDto data) {
        return service.createAsync(data)
                      .thenApply(v -> ResponseEntity.ok(""))
                      .join();
    }

    @GetMapping("{id}")
    public ResponseEntity<String> getUser(@PathVariable long id) {
        return null;
    }
}
