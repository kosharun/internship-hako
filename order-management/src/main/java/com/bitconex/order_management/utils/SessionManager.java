package com.bitconex.order_management.utils;

import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    private String currentUserRole;

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(String role) {
        this.currentUserRole = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(currentUserRole);
    }

    public boolean isUser() {
        return "USER".equalsIgnoreCase(currentUserRole);
    }
}
