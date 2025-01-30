package com.project.app.service;

import com.project.app.entity.User;
import com.project.app.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private LoginRepository loginRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public String signUpUser(User user) {
        if(loginRepository.countByUserName(user.getUsername())>0){
            return "User already exist with username: "+ user.getUsername();
        }
        if(loginRepository.countByPhoneNumber(user.getPhoneNumber())>0){
            return "User already exist with phoneNumber: "+ user.getPhoneNumber();
        }
        if(loginRepository.countByEmail(user.getEmail())>0){
            return "User already exist with email: "+ user.getEmail();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        loginRepository.save(user);
        return "User signed up successfully!";
    }
}
