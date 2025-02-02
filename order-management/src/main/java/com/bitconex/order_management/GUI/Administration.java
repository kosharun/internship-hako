package com.bitconex.order_management.GUI;

import java.util.Scanner;

public class Administration {
    private final Scanner scanner = new Scanner(System.in);

    public void startAdminConsole() {
        while (true) {
            System.out.println("\n🔹 ADMIN PANEL 🔹");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage Products");
            System.out.println("3. View All Orders");
            System.out.println("4. Exit");

            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    manageUsers();
                    break;
                case 2:
                    manageProducts();
                    break;
                case 3:
                    listAllOrders();
                    break;
                case 4:
                    System.out.println("Exiting Admin Panel...");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private void manageUsers() {
        System.out.println("User Management (TODO: Implement CRUD operations here).");
    }

    private void manageProducts() {
        System.out.println("Product Management (TODO: Implement CRUD operations here).");
    }

    private void listAllOrders() {
        System.out.println("Order Listing (TODO: Implement order export feature here).");
    }
}
