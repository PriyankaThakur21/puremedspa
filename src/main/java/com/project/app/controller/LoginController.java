package com.project.app.controller;

import com.project.app.entity.AdminRequestEntity;
import com.project.app.dto.MasterResponseDto;
import com.project.app.entity.User;
import com.project.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class LoginController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<MasterResponseDto<User>> signUp(@RequestBody User user) {
        try {
            log.info("Attempting to sign up user with username: {}", user.getUsername());
            String result = userService.signUpUser(user);

            if (result.contains("User already exists")) {
                log.warn("Signup failed: {}", result);
                MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.UNAUTHORIZED.value(), false, result);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            log.info("User signed up successfully: {}", user.getUsername());
            MasterResponseDto<User> response = new MasterResponseDto<>(user, HttpStatus.CREATED.value(), true, result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    catch(Exception e){
        log.error("Error occurred during signup: {}", e.getMessage(), e);
        MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred during signup. Please try again later.");
        return ResponseEntity.status(500).body(response);
        }
}

    @PostMapping("/login")
    public ResponseEntity<MasterResponseDto<User>> login(@RequestBody User user){
        try{
            log.info("Attempting to log in user with username: {}", user.getUsername());
            String result = userService.loginUser(user);
            if(result.contains("Invalid")){
                log.warn("Login failed for username: {}: {}", user.getUsername(), result);
                MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.UNAUTHORIZED.value(), false, result);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            log.info("Login successful for username: {}", user.getUsername());
            MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.OK.value(), true, result);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch(Exception e){
            log.error("Error occurred during login: {}", e.getMessage(), e);
            MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred during signup. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/forgotPassword")
    public ResponseEntity<MasterResponseDto<User>> forgotPassword(@RequestBody User user) {
        try {
            log.info("Attempting to reset password for user with username: {}", user.getUsername());
            String result = userService.forgetPassword(user);

            if (result.equals("User not found")) {
                log.warn("Password reset failed for username: {}: {}", user.getUsername(), result);
                MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.NOT_FOUND.value(), false, result);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            log.info("Password reset successful for username: {}", user.getUsername());
            MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.OK.value(), true, result);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error occurred during password reset: {}", e.getMessage(), e);
            MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred during the password reset process. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addUser")
    public ResponseEntity<MasterResponseDto<User>> addUser(@RequestBody AdminRequestEntity adminRequest) {
        try {
            log.info("Attempting to add user by admin: {}", adminRequest.getAdminUsername());
            String result = userService.addUser(adminRequest);

            if (result.contains("does not exist") || result.contains("don't have permission")) {
                log.warn("User addition failed for admin {}: {}", adminRequest.getAdminUsername(), result);
                MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.UNAUTHORIZED.value(), false, result);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            log.info("User added successfully by admin: {}", adminRequest.getAdminUsername());
            MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.CREATED.value(), true, result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error occurred while adding user by admin {}: {}", adminRequest.getAdminUsername(), e.getMessage(), e);
            MasterResponseDto<User> response = new MasterResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred while adding the user. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
