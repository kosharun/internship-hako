package com.bitconex.order_management.GUI.ADMIN;

import com.bitconex.order_management.service.OrderService;
import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import com.bitconex.order_management.entity.Order;
import com.bitconex.order_management.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.bitconex.order_management.utils.ConsoleUtil.*;

@Component
public class OrderAdministrationGUI {
    private final Scanner scanner = new Scanner(System.in);
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderAdministrationGUI(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    public void startOrderAdminConsole() {
        while (true) {
            int choice;
            while (true) {
                try {
                    print("\n🔹 ORDER ADMINISTRATION 🔹");
                    print("1. View All Orders");
                    print("2. Update Order Status");
                    print("3. Export list of all orders in CSV");
                    print("4. Exit");

                    print("Select an option: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    break;
                } catch (Exception e) {
                    printError("The input should be a number!");
                    scanner.nextLine();
                }
            }

            switch (choice) {
                case 1:
                    try {
                        viewAllOrders();
                    } catch (Exception e) {
                        printError("Error fetching orders - " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        updateOrderStatus();
                    } catch (Exception e) {
                        printError("Error updating order - " + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        exportAllOrdersInCSV();
                    } catch (Exception e) {
                        printError("Error exporting orders - " + e.getMessage());
                    }
                    break;
                case 4:
                    print("Exiting...");
                    return;

                default:
                    printError("Invalid choice. Try again.");
            }
        }
    }

    public void exportAllOrdersInCSV() {
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            printError("No orders to export!");
            return;
        }

        print("Enter folder path to save the CSV file (leave empty for default 'orderCSVs' folder): ");
        String inputPath = scanner.nextLine().trim();

        String folderName = inputPath.isEmpty() ? "orderCSVs" : inputPath;
        File folder = new File(folderName);

        if (!folder.exists() && !folder.mkdirs()) {
            printError("Failed to create directory: " + folderName);
            return;
        }

        String fileName = folderName + "/orders_export_" +
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(java.time.LocalDateTime.now()) + ".csv";

        try (PrintWriter writer = new PrintWriter(fileName)) {

            // CSV Headers
            writer.println("Order ID,User ID,Total Price,Status,Order Date");

            // CSV Data Rows
            for (Order order : orders) {
                String csvRow = String.format("%d,%d,%.2f,%s,%s",
                        order.getOrderId(),
                        order.getUser().getUserId(),
                        order.getTotalPrice(),
                        order.getStatus().getName(),
                        order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                writer.println(csvRow);
            }

            printSuccess("✅ Orders successfully exported to: " + fileName);

        } catch (FileNotFoundException e) {
            printError("Error creating file: " + e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error during export: " + e.getMessage());
        }
    }



    private void viewAllOrders() {
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            printError("No orders found!");
            return;
        }

        List<Map<String, String>> displayData = new ArrayList<>();
        for (Order order : orders) {
            Map<String, String> row = new HashMap<>();
            row.put("Order ID", String.valueOf(order.getOrderId()));
            row.put("User ID", String.valueOf(order.getUser().getUserId()));
            row.put("Total Price", String.format("$%.2f", order.getTotalPrice()));
            row.put("Status", order.getStatus().getName());
            row.put("Order Date", order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            displayData.add(row);
        }

        String table = AsciiTable.getTable(displayData, Arrays.asList(
                new Column().header("Order ID").headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER).with(row -> row.get("Order ID")),
                new Column().header("User ID").headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER).with(row -> row.get("User ID")),
                new Column().header("Total Price").headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.RIGHT).with(row -> row.get("Total Price")),
                new Column().header("Status").headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER).with(row -> row.get("Status")),
                new Column().header("Order Date").headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.LEFT).with(row -> row.get("Order Date"))
        ));

        print("\n🔹 List of Orders 🔹");
        print(table);
    }

    private void updateOrderStatus() {
        viewAllOrders();
        print("Enter Order ID: ");
        Long orderId = scanner.nextLong();
        scanner.nextLine();

        Order order = orderService.getOrderById(orderId);

        print("Enter New Status(completed, cancelled, pending) : ");
        String status = scanner.nextLine().toLowerCase();

        if (!status.matches("completed|cancelled|pending")) {
            printError("Invalid status. Please enter 'completed', 'cancelled', or 'pending'.");
            return;
        }

        orderService.updateOrderStatus(order, status);

        print("Order status updated successfully!");
    }
}
