package com.example.sortitems.controller;

import com.example.sortitems.model.User;
import com.example.sortitems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Logger;

@Controller
public class RegistrationController {

    private static final Logger logger = Logger.getLogger(RegistrationController.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm() {
        logger.info("Showing registration form");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, 
                               @RequestParam String password, 
                               Model model) {
        logger.info("Processing registration for username: " + username);
        try {
            if (userRepository.findByUsername(username).isPresent()) {
                logger.warning("Username already exists: " + username);
                model.addAttribute("error", "Username already exists!");
                return "register";
            }

            User user = new User(username, passwordEncoder.encode(password), "ROLE_USER");
            userRepository.save(user);
            logger.info("User registered successfully: " + username);
            return "redirect:/login?registered";
        } catch (Exception e) {
            logger.severe("Error during registration: " + e.getMessage());
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}