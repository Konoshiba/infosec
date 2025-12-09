package com.infosec.config;

import com.infosec.entity.User;
import com.infosec.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create a test user if no users exist
        if (userRepository.count() == 0) {
            User testUser = new User();
            testUser.setUsername("testuser");
            // Password will be hashed with bcrypt
            testUser.setPassword(passwordEncoder.encode("testpass123"));
            testUser.setEmail("test@example.com");
            testUser.setFullName("Test User");
            userRepository.save(testUser);

            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@example.com");
            adminUser.setFullName("Administrator");
            userRepository.save(adminUser);

            LOGGER.info("Test users created: testuser/testpass123 and admin/admin123");
        }
    }
}
