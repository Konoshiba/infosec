package com.infosec.controller;

import com.infosec.dto.DataResponse;
import com.infosec.dto.UserResponse;
import com.infosec.service.UserService;
import com.infosec.util.XssSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private UserService userService;

    @Autowired
    private XssSanitizer xssSanitizer;

    @GetMapping("/data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DataResponse> getData() {
        List<UserResponse> users = userService.getAllUsers();
        
        // Sanitize message to prevent XSS
        String message = xssSanitizer.sanitize("List of users retrieved successfully");
        
        DataResponse response = new DataResponse(message, users);
        return ResponseEntity.ok(response);
    }
}

