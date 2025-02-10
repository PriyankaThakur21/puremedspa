package com.project.app.service;
import com.project.app.dto.AdminRequestDto;
import com.project.app.entity.User;
import com.project.app.repository.LoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private final LoginRepository loginRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    public UserService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public String signUpUser(User user) {
        log.info("Inside signUpUser method");
        try {
            Optional<User> existingUser = loginRepository.findByUserNameOrPhoneNumberOrEmail(user.getUsername(), user.getPhoneNumber(), user.getEmail());

            if (existingUser.isPresent()) {
                System.out.println("foundUser   "+ existingUser.get());
                User foundUser = existingUser.get(); // Get the existing user from Optional
                // Check if the username is already taken
                if (foundUser.getUsername().equals(user.getUsername())) {
                    log.warn("Username already exists: {}", user.getUsername());
                    return "User already exists with username: " + user.getUsername();
                }

                // Check if the phone number is already taken
                if (foundUser.getPhoneNumber().equals(user.getPhoneNumber())) {
                    log.warn("Phone number already exists: {}", user.getPhoneNumber());
                    return "User already exists with phone number: " + user.getPhoneNumber();
                }

                // Check if the email is already taken
                if (foundUser.getEmail().equals(user.getEmail())) {
                    log.warn("Email already exists: {}", user.getEmail());
                    return "User already exists with email: " + user.getEmail();
                }
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            loginRepository.save(user);
            log.info("User signed up successfully with username: {}", user.getUsername());
            return "User added successfully!";
        }
        catch(Exception e){
            log.error("Error occurred during signup for username: {}. Error: {}", user.getUsername(), e.getMessage());
            return "Exception in saving data"+ e.getMessage();
        }
    }

    public String loginUser(User user){
        log.info("Inside loginUser method");
        try {
            Optional<User> existingUser = loginRepository.findByUsername(user.getUsername());
            if (existingUser.isEmpty()) {
                log.warn("Invalid username attempt: {}", user.getUsername());
                return "Invalid username";
            }
            if (!passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
                log.warn("Invalid password attempt for username: {}", user.getUsername());
                return "Invalid password";
            }
            log.info("Login successful for username: {}", user.getUsername());
            return "Login successful";
        }
        catch(Exception e){
            log.error("Error occurred during login for username: {}. Error: {}", user.getUsername(), e.getMessage());
            return "Exception in login"+ e;
        }
    }

    public String forgetPassword(User user) {
        log.info("Inside forgetPassword method");
        try {
            Optional<User> existingUser = loginRepository.findByUsername(user.getUsername());
            if (existingUser.isEmpty()) {
                log.warn("User not found for username: {}", user.getUsername());
                return "User not found";
            }
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            existingUser.get().setPassword(encodedPassword);
            loginRepository.save(existingUser.get());
            log.info("Password updated successfully for username: {}", user.getUsername());
            return "Password updated successfully";
        } catch (Exception e) {
            log.error("Error occurred while resetting password for username: {}. Error: {}", user.getUsername(), e.getMessage());
            return "Exception in saving new password" + e;
        }
    }

    public String addUser(AdminRequestDto adminRequest) {
        log.info("Inside addUser method");
        try {
            Optional<User> existingUser = loginRepository.findByUsername(adminRequest.getAdminUsername());
            if (existingUser.isEmpty()) {
                log.warn("Admin username not found: {}", adminRequest.getAdminUsername());
                return "User does not exist with username: " + adminRequest.getAdminUsername();
            }
            if (!existingUser.get().getRole().equals("ADMIN")) {
                log.warn("Permission denied for username: {}. Not an admin.", adminRequest.getAdminUsername());
                return "You don't have permission to add users.";
            }
            User user = adminRequest.getUser();
            String savedUser = signUpUser(user);
            log.info("Admin username: {} added a new user with username: {}", adminRequest.getAdminUsername(), user.getUsername());
            return savedUser;
        } catch (Exception e) {
            log.error("Error occurred while adding user. Admin username: {}. Error: {}", adminRequest.getAdminUsername(), e.getMessage());
            return "Error occurred while adding user: " + e.getMessage();
        }
    }
}
