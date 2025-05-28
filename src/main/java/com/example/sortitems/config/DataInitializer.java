package com.example.sortitems.config;

import com.example.sortitems.model.User;
import com.example.sortitems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Tambahkan pengguna admin
        if (!userRepository.findByUsername("admin").isPresent()) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), "ROLE_ADMIN");
            userRepository.save(admin);
        }

        // Tambahkan pengguna biasa
        if (!userRepository.findByUsername("user").isPresent()) {
            User user = new User("user", passwordEncoder.encode("user123"), "ROLE_USER");
            userRepository.save(user);
        }
    }
}