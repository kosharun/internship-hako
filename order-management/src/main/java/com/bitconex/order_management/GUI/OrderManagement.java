package com.bitconex.order_management.GUI;

import java.util.Scanner;

public class OrderManagement {
    private final Scanner scanner = new Scanner(System.in);

    public void startOrderConsole() {
        while (true) {
            System.out.println("\n🔹 ORDER MANAGEMENT 🔹");
            System.out.println("1. View My Orders");
            System.out.println("2. Create New Order");
            System.out.println("3. Exit");

            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewOrders();
                    break;
                case 2:
                    createOrder();
                    break;
                case 3:
                    System.out.println("Exiting Order Panel...");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private void viewOrders() {
        System.out.println("Viewing Orders (TODO: Fetch orders from DB).");
    }

    private void createOrder() {
        System.out.println("Creating New Order (TODO: Implement order creation process).");
    }
}
