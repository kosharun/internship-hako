package com.bitconex.order_management.GUI.ADMIN;

import org.springframework.stereotype.Component;

import java.util.Scanner;

import static com.bitconex.order_management.utils.ConsoleUtil.print;
import static com.bitconex.order_management.utils.ConsoleUtil.printError;

@Component
public class MainAdmin {
    private final Scanner scanner = new Scanner(System.in);
    private final Administration administration;

    public MainAdmin(Administration administration) {
        this.administration = administration;
    }


    public void startAdminConsole() {
        while (true) {

            int choice;
            while (true) {
                try {
                    print("\n🔹 ADMIN PANEL 🔹");
                    print("1. User Administration");
                    print("2. Product Catalog");
                    print("3. Export list of all orders in CSV");
                    print("4. Exit");

                    print("Select an option: ");
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume leftover newline
                    break;
                } catch (Exception e) {
                    printError("The input should be a number!");
                    scanner.nextLine(); // 🔹 Clear invalid input to avoid infinite loop
                }
            }

            switch (choice) {
                case 1:
                    try {
                        administration.startUserAdminConsole();
                    } catch (Exception e) {
                        printError("Error creating user - " + e.getMessage());
                    }

                    break;
                case 2:
                    try {

                    } catch (Exception e) {
                        printError("Error fetching users - " + e.getMessage());
                    }
                    break;
                case 3:

                    break;
                case 4:
                    print("Exiting Admin Panel...");
                    return;
                default:
                    printError("Invalid choice. Try again.");
            }
        }
    }

}
