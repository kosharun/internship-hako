package com.bitconex.order_management.GUI;


import com.bitconex.order_management.GUI.ADMIN.MainAdminGUI;
import com.bitconex.order_management.GUI.USER.MainUserGUI;
import com.bitconex.order_management.service.UserService;
import com.bitconex.order_management.utils.SessionManager;
import org.springframework.stereotype.Component;

import static com.bitconex.order_management.utils.ConsoleUtil.*;

import java.util.Scanner;

@Component
public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final SessionManager sessionManager;
    private final MainAdminGUI mainAdminGUI;
    private final MainUserGUI mainUserGUI;

    public MainMenu(UserService userService, SessionManager sessionManager, MainAdminGUI mainAdminGUI, MainUserGUI mainUserGUI) {
        this.userService = userService;
        this.sessionManager = sessionManager;
        this.mainAdminGUI = mainAdminGUI;
        this.mainUserGUI = mainUserGUI;
    }

    public void start() {
        print("Welcome to Order Management System! Please Sign In.");
        String role = "";
        Long userId = null;
        sessionManager.setCurrentUserId(userId);
        sessionManager.setCurrentUserRole(role);
        while (role.isEmpty()) {
            try {
                print("Enter Username: ");
                String username = scanner.nextLine();

                print("Enter Password: ");
                String password = scanner.nextLine();

                // Simulate role checking (Replace with real authentication logic)
                role = userService.login(username, password).getRole();
                userId = userService.login(username, password).getUserId();
            } catch (Exception e) {
                printError("Error logging in: " + e.getMessage());
            }

            print("");
            if (role.isEmpty()) {
                print("Please try again");
            }
        }

        sessionManager.setCurrentUserId(userId);
        sessionManager.setCurrentUserRole(role);

        if("ADMIN".matches(role)) {
            mainAdminGUI.startAdminConsole();
        } else if ("USER".matches(role)) {
            mainUserGUI.startUserConsole();
        }

    }
}
