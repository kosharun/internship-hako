package com.bitconex.order_management.GUI.USER;

import org.springframework.stereotype.Component;

import java.util.Scanner;

import static com.bitconex.order_management.utils.ConsoleUtil.print;
import static com.bitconex.order_management.utils.ConsoleUtil.printError;

@Component
public class MainUserGUI {
    private final Scanner scanner = new Scanner(System.in);

    public void startUserConsole() {
        while (true) {
            int choice;
            while (true) {
                try {
                    print("\n🔹 USER PANEL 🔹");
                    print("1. List of my orders");
                    print("2. Order now");
                    print("3. Exit");

                    print("Select an option: ");
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume leftover newline
                    break;
                } catch (Exception e) {
                    printError("The input should be a number!");
                    scanner.nextLine(); // Clear invalid input to avoid infinite loop
                }
            }

            switch (choice) {
                case 1:
                    try {
                        print("List of all ORDERS");
                    } catch (Exception e) {
                        printError("Error listing orders - " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        print("Order now");
                    } catch (Exception e) {
                        printError("Error processing order - " + e.getMessage());
                    }
                    break;
                case 3:
                    print("Exiting...");
                    return;
                default:
                    printError("Invalid option!");
            }
        }
    }
}