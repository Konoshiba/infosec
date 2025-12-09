package com.infosec.service;

import com.infosec.dto.UserResponse;
import com.infosec.entity.User;
import com.infosec.repository.UserRepository;
import com.infosec.util.XssSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private XssSanitizer xssSanitizer;

    public UserResponse getUserById(Long id) {
        // Spring Data JPA uses parameterized queries automatically
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Sanitize user data before returning to prevent XSS
        return new UserResponse(
                user.getId(),
                xssSanitizer.sanitize(user.getUsername()),
                user.getEmail() != null ? xssSanitizer.sanitize(user.getEmail()) : null,
                user.getFullName() != null ? xssSanitizer.sanitize(user.getFullName()) : null
        );
    }

    public List<UserResponse> getAllUsers() {
        // Spring Data JPA uses parameterized queries automatically
        List<User> users = userRepository.findAll();

        // Sanitize all user data before returning
        return users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        xssSanitizer.sanitize(user.getUsername()),
                        user.getEmail() != null ? xssSanitizer.sanitize(user.getEmail()) : null,
                        user.getFullName() != null ? xssSanitizer.sanitize(user.getFullName()) : null
                ))
                .collect(Collectors.toList());
    }
}

