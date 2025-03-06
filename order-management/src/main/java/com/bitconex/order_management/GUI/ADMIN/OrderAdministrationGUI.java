package com.bitconex.order_management.GUI.ADMIN;

import com.bitconex.order_management.service.OrderService;
import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import com.bitconex.order_management.entity.Order;
import com.bitconex.order_management.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;
import static com.bitconex.order_management.utils.ConsoleUtil.print;
import static com.bitconex.order_management.utils.ConsoleUtil.printError;

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
                    print("3. Exit");

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
                    print("Exiting...");
                    return;
                default:
                    printError("Invalid choice. Try again.");
            }
        }
    }

    private void viewAllOrders() {
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            print("❌ No orders found!");
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
