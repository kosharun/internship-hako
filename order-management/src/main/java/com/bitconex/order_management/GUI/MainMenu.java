package com.bitconex.order_management.GUI;


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
    private final Administration administrationConsole;

    public MainMenu(UserService userService, SessionManager sessionManager, Administration administrationConsole) {
        this.userService = userService;
        this.sessionManager = sessionManager;
        this.administrationConsole = administrationConsole;
    }

    public void start() {
        print("Welcome to Order Management System!");
        String role = "";
        while (role.isEmpty()) {
            print("Enter Username: ");
            String username = scanner.nextLine();

            print("Enter Password: ");
            String password = scanner.nextLine();

            // Simulate role checking (Replace with real authentication logic)
            role = userService.login(username, password);

            print("");
            if (role.isEmpty()) {
                print("Please try again");
            }
        }

        sessionManager.setCurrentUserRole(role);

        if("ADMIN".matches(role)) {
            administrationConsole.startAdminConsole();
            printSuccess("Logged in as " + sessionManager.getCurrentUserRole());
        }

    }
}
