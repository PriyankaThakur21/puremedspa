package com.project.app.service;

import com.project.app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Check if the username exists
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{username}, Integer.class);
        return count != null && count > 0;
    }

    // Check if the email exists
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        return count != null && count > 0;
    }

    // Check if the phone number exists
    public boolean existsByPhoneNumber(String phoneNumber) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone_number = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{phoneNumber}, Integer.class);
        return count != null && count > 0;
    }

    // Save user to the database
    public String signUpUser(User user) {
        // Check if the username, email, or phone number already exists
        if (existsByUsername(user.getUsername())) {
            return "Username already exists!";
        }
        if (existsByEmail(user.getEmail())) {
            return "Email already exists!";
        }
        if (existsByPhoneNumber(user.getPhoneNumber())) {
            return "Phone number already exists!";
        }

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set creation and update times
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        // SQL query to insert the user into the database
        String sql = "INSERT INTO users (username, password, first_name, last_name, email, phone_number, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Execute the insert query
        int result = jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhoneNumber(),
                new java.sql.Date(user.getCreatedAt().getTime()), new java.sql.Date(user.getUpdatedAt().getTime()));

        if (result > 0) {
            return "User signed up successfully!";
        } else {
            return "Error occurred during sign up.";
        }
    }
}
