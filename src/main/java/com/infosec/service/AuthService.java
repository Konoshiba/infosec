package com.infosec.service;

import com.infosec.dto.LoginRequest;
import com.infosec.dto.LoginResponse;
import com.infosec.entity.User;
import com.infosec.repository.UserRepository;
import com.infosec.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse authenticate(LoginRequest loginRequest) {
        // Find user by username (uses parameterized query - SQL injection safe)
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // Verify password using bcrypt
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(token, user.getUsername());
    }
}
