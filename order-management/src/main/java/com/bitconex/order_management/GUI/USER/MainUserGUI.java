package com.bitconex.order_management.GUI.USER;

import com.bitconex.order_management.GUI.ADMIN.ProductCatalogGUI;
import com.bitconex.order_management.dto.*;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.service.OrderItemService;
import com.bitconex.order_management.service.OrderService;
import com.bitconex.order_management.service.ProductService;
import com.bitconex.order_management.utils.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.bitconex.order_management.utils.ConsoleUtil.print;
import static com.bitconex.order_management.utils.ConsoleUtil.printError;

@Component
public class MainUserGUI {
    private final Scanner scanner = new Scanner(System.in);
    private final OrderService orderService;
    private final ProductService productService;
    private final SessionManager sessionManager;
    private final ProductCatalogGUI productCatalogGUI;
    private final OrderItemService orderItemService;


    public MainUserGUI(OrderService orderService, ProductService productService, SessionManager sessionManager, ProductCatalogGUI productCatalogGUI, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.productService = productService;
        this.sessionManager = sessionManager;
        this.productCatalogGUI = productCatalogGUI;
        this.orderItemService = orderItemService;
    }

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
                        Long userId = sessionManager.getCurrentUserId();
                        getAllOrders(userId);
                    } catch (Exception e) {
                        printError("Error listing orders - " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        orderNow();
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

    void getAllOrders(Long userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Za podršku za LocalDate
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Lijep format

        try {
            List<OrderDTO> orders = orderService.getAllOrdersByUserId(userId);
            String jsonOutput = objectMapper.writeValueAsString(orders);
            print(jsonOutput);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving orders: " + e.getMessage());
        }
    }


    void orderNow() {
        try {
            print("Available products: ");
            productCatalogGUI.getAllProducts();

            List<ProductOrder> orderItems = new ArrayList<>();
            double totalPrice = 0.0;

            while (true) {
                Long productId = null;
                int quantity = 0;

                // Get valid product ID
                while (true) {
                    print("Enter the product ID (or type 'done' to finish): ");
                    if (scanner.hasNextLong()) {
                        productId = scanner.nextLong();
                        scanner.nextLine(); // Consume newline
                        break;
                    } else if (scanner.hasNext("done")) {
                        scanner.nextLine(); // Consume "done" input
                        break;
                    } else {
                        printError("Invalid input. Please enter a valid product ID (numeric value) or 'done' to finish.");
                        scanner.nextLine(); // Clear invalid input
                    }
                }

                if (productId == null) {
                    break; // User finished adding items
                }

                // Get valid quantity
                while (true) {
                    print("Enter the quantity for product ID " + productId + ": ");
                    if (scanner.hasNextInt()) {
                        quantity = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        if (quantity > 0) {
                            break;
                        } else {
                            printError("Quantity must be greater than zero.");
                        }
                    } else {
                        printError("Invalid input. Please enter a valid quantity.");
                        scanner.nextLine(); // Clear invalid input
                    }
                }

                // Fetch product price (assuming a method exists)
                Product product = productService.getProductById(productId);
                double productPrice = product.getPrice();
                double itemTotalPrice = productPrice * quantity;

                // Add item to the list and update total price
                orderItems.add(new ProductOrder(productId, quantity));
                totalPrice += itemTotalPrice;

                print("✅ Added product ID " + productId + " with quantity " + quantity + " (Subtotal: $" + itemTotalPrice + ")");
            }

            if (orderItems.isEmpty()) {
                printError("No products added to the order. Order cancelled.");
                return;
            }

            // Confirm order
            print("Type 'finish' or 'cancel' to confirm or cancel the order: ");
            String finish = scanner.nextLine().trim().toLowerCase();

            if (finish.equals("cancel")) {
                print("Order cancelled.");
                return;
            } else if (!finish.equals("finish")) {
                printError("Invalid input. Order cancelled.");
                return;
            }

            // Create order request DTO
            Long userId = sessionManager.getCurrentUserId();
            OrderRequestDTO orderRequest = new OrderRequestDTO(userId, totalPrice);

            // Create order and get order ID
            OrderDTO newOrder = orderService.createOrder(orderRequest);

            Long orderId = newOrder.getOrderId();

            // Create order items using the generated order ID
            List<OrderItemRequestDTO> finalOrderItems = new ArrayList<>();
            for (ProductOrder item : orderItems) {
                finalOrderItems.add(OrderItemRequestDTO.builder()
                        .orderId(orderId)
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build());
            }

            // Save order items by looping through the list

            for (OrderItemRequestDTO orderItem : finalOrderItems) {
                orderItemService.createOrderItem(orderItem);
            }

            print("✅ Order placed successfully!");
            print("Order ID: " + orderId);
            print("Total Price: $" + totalPrice);

        } catch (Exception e) {
            printError("Error processing order - " + e.getMessage());
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static
    class ProductOrder {
        private Long productId;
        private int quantity;
    }



}