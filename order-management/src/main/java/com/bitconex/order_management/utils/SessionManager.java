package com.bitconex.order_management.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class SessionManager {
    private String currentUserRole;
    private Long currentUserId;

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(currentUserRole);
    }

    public boolean isUser() {
        return "USER".equalsIgnoreCase(currentUserRole);
    }
}
