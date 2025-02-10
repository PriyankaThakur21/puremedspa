package com.project.app.dto;

import com.project.app.entity.User;

public class AdminRequestDto {
    private String adminUsername;
    private User user;

    public String getAdminUsername() {
        return adminUsername;
    }

    public User getUser() {
        return user;
    }
}