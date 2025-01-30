package com.project.app.controller;

import com.project.app.entity.User;
import com.project.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public String signUp(@RequestBody User user) {
        return userService.signUpUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        return userService.loginUser(user);
    }
    @PostMapping("/forgotPassword")
        public String forgetPassword(@RequestBody User user){
        return userService.forgetPassword(user);
        }

     @PostMapping("/addUser/{adminUserName}")
    public String addUser(@PathVariable String adminUserName, @RequestBody User user){
        return userService.addUser(adminUserName, user);
     }
}
