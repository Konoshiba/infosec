package com.infosec.controller;

import com.infosec.dto.UserResponse;
import com.infosec.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        // Spring automatically validates path variable
        // UserService uses parameterized queries (SQL injection safe)
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}

