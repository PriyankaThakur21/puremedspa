package com.project.app.service;
import com.project.app.entity.User;
import com.project.app.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private LoginRepository loginRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public String signUpUser(User user) {
        try {
            if (loginRepository.countByUserName(user.getUsername()) > 0) {
                return "User already exist with username: " + user.getUsername();
            }
            if (loginRepository.countByPhoneNumber(user.getPhoneNumber()) > 0) {
                return "User already exist with phoneNumber: " + user.getPhoneNumber();
            }
            if (loginRepository.countByEmail(user.getEmail()) > 0) {
                return "User already exist with email: " + user.getEmail();
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            loginRepository.save(user);
            return "User added successfully!";
        }
        catch(Exception e){
            return "Exception in saving data"+ e;
        }
    }

    public String loginUser(User user){
            User existingUser = loginRepository.findByUsername(user.getUsername());
            if (existingUser == null) {
                return "Invalid username";
            }
            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                return "Invalid password";
            }
            return "Login successful";
    }

    public String forgetPassword(User user) {
        try {
            Optional<User> existingUser = Optional.ofNullable(loginRepository.findByUsername(user.getUsername()));
            if (existingUser.isEmpty()) {
                return "User does not exist";
            }
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            existingUser.get().setPassword(encodedPassword);
            loginRepository.save(existingUser.get());
            return "Password updated successfully";
        } catch (Exception e) {
            return "Exception in saving new password" + e;
        }
    }

    public String addUser(String adminUserName, User user) {
        try {
            Optional<User> existingUser = Optional.ofNullable(loginRepository.findByUsername(adminUserName));
            if (existingUser.isEmpty()) {
                return "User does not exist with userName: " + adminUserName;
            }
            if (!existingUser.get().getRole().equals("ADMIN")) {
                return "You don't have permission";
            }
            System.out.println(user.toString());
            return signUpUser(user);
        } catch (Exception e) {
            return "Error occurred while saving user" + e;
        }
    }
}
