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
import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.bitconex.order_management.utils.ConsoleUtil.*;

@Component
public class MainUserGUI {
    private final Scanner scanner = new Scanner(System.in);
    private final OrderService orderService;
    private final ProductService productService;
    private final SessionManager sessionManager;
    private final OrderItemService orderItemService;

    public MainUserGUI(OrderService orderService, ProductService productService, SessionManager sessionManager, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.productService = productService;
        this.sessionManager = sessionManager;
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
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            List<OrderDTO> orders = orderService.getAllOrdersByUserId(userId);
            String jsonOutput = objectMapper.writeValueAsString(orders);
            print(jsonOutput);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving orders: " + e.getMessage());
        }
    }

    void getAllAvailableProducts() {
        List<Product> products = productService.getAllAvailableProducts();
        if (products.isEmpty()) {
            printInfo("No available products!");
            return;
        }

        String table = AsciiTable.getTable(products, Arrays.asList(
                new Column().header("ID").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.CENTER)
                        .with(product -> String.valueOf(product.getProductId())),
                new Column().header("Catalog").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.LEFT)
                        .with(product -> product.getCatalog() != null ? product.getCatalog().getName() : "N/A"),
                new Column().header("Name").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.LEFT)
                        .with(Product::getName),
                new Column().header("Description").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.LEFT)
                        .with(product -> product.getDescription() != null ? product.getDescription() : "N/A"),
                new Column().header("Price").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.RIGHT)
                        .with(product -> String.format("%.2f", product.getPrice())),
                new Column().header("Published").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.CENTER)
                        .with(product -> product.getDatePublished() != null ? product.getDatePublished().toString() : "N/A"),
                new Column().header("Available Until").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.CENTER)
                        .with(product -> product.getAvailableUntil() != null ? product.getAvailableUntil().toString() : "Indefinite"),
                new Column().header("Stock").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.CENTER)
                        .with(product -> String.valueOf(product.getStockQuantity()))
        ));

        print(table);
    }

    void orderNow() {
        try {
            print("Available products: ");
            getAllAvailableProducts();

            List<ProductOrder> orderItems = new ArrayList<>();
            double totalPrice = 0.0;

            while (true) {
                Long productId = null;
                int quantity = 0;

                while (true) {
                    print("Enter the product ID (or type 'done' to finish): ");
                    if (scanner.hasNextLong()) {
                        productId = scanner.nextLong();
                        scanner.nextLine();
                        try {
                            productService.getProductById(productId);
                            break;
                        } catch (RuntimeException e) {
                            printError("Product with ID " + productId + " not found.");
                        }
                    } else if (scanner.hasNext("done")) {
                        scanner.nextLine();
                        break;
                    } else {
                        printError("Invalid input. Please enter a valid product ID (numeric value) or 'done' to finish.");
                        scanner.nextLine();
                    }
                }

                if (productId == null) {
                    break;
                }

                Product product = productService.getProductById(productId);

                while (true) {
                    print("Enter the quantity for product ID " + productId + ": ");
                    if (scanner.hasNextInt()) {
                        quantity = scanner.nextInt();
                        scanner.nextLine();
                        if (quantity > 0) {
                            break;
                        } else {
                            printError("Quantity must be greater than zero.");
                        }
                    } else {
                        printError("Invalid input. Please enter a valid quantity.");
                        scanner.nextLine();
                    }
                }

                Long finalProductId = productId;
                ProductOrder existingProductOrder = orderItems.stream()
                        .filter(item -> item.getProductId().equals(finalProductId))
                        .findFirst()
                        .orElse(null);

                if (existingProductOrder != null) {
                    int newQuantity = existingProductOrder.getQuantity() + quantity;
                    if (newQuantity > product.getStockQuantity()) {
                        printError("Not enough stock available! You already have " + existingProductOrder.getQuantity() + " in cart. Only " + product.getStockQuantity() + " left.");
                        continue;
                    }
                    existingProductOrder.setQuantity(newQuantity);
                    print("✅ Updated quantity of product ID " + productId + " to " + newQuantity);
                } else {
                    if (quantity > product.getStockQuantity()) {
                        printError("Not enough stock available! Only " + product.getStockQuantity() + " left.");
                        continue;
                    }
                    orderItems.add(new ProductOrder(productId, quantity));
                    print("✅ Added product ID " + productId + " with quantity " + quantity);
                }

                double productPrice = product.getPrice();
                totalPrice += productPrice * quantity;
                print("✅ Added product ID " + productId + " with quantity " + quantity + " (Subtotal: $" + totalPrice + ")");
            }

            if (orderItems.isEmpty()) {
                printError("No products added to the order. Order cancelled.");
                return;
            }

            print("\n🔹 Your Order Summary 🔹");

            List<Map<String, String>> displayData = new ArrayList<>();
            for (ProductOrder item : orderItems) {
                Product product = productService.getProductById(item.getProductId());
                Map<String, String> row = new HashMap<>();
                row.put("Product ID", String.valueOf(item.getProductId()));
                row.put("Product Name", product.getName());
                row.put("Quantity", String.valueOf(item.getQuantity()));
                row.put("Subtotal", String.format("$%.2f", product.getPrice() * item.getQuantity()));
                displayData.add(row);
            }

            String table = AsciiTable.getTable(displayData, Arrays.asList(
                    new Column().header("Product ID").headerAlign(HorizontalAlign.CENTER)
                            .dataAlign(HorizontalAlign.CENTER)
                            .with(row -> row.get("Product ID")),
                    new Column().header("Product Name").headerAlign(HorizontalAlign.CENTER)
                            .dataAlign(HorizontalAlign.LEFT)
                            .with(row -> row.get("Product Name")),
                    new Column().header("Quantity").headerAlign(HorizontalAlign.CENTER)
                            .dataAlign(HorizontalAlign.CENTER)
                            .with(row -> row.get("Quantity")),
                    new Column().header("Subtotal").headerAlign(HorizontalAlign.CENTER)
                            .dataAlign(HorizontalAlign.RIGHT)
                            .with(row -> row.get("Subtotal"))
            ));

            print(table);
            print("💰 Total Price: $" + String.format("%.2f", totalPrice));
            print("\nType 'finish' to confirm or 'cancel' to cancel the order: ");
            String finish = scanner.nextLine().trim().toLowerCase();

            if ("cancel".equals(finish)) {
                print("❌ Order cancelled.");
                return;
            } else if (!"finish".equals(finish)) {
                printError("Invalid input. Order cancelled.");
                return;
            }

            print("✅ Order confirmed! Processing your order...");
            Long userId = sessionManager.getCurrentUserId();
            OrderRequestDTO orderRequest = new OrderRequestDTO(userId, totalPrice);
            OrderDTO newOrder = orderService.createOrder(orderRequest);
            Long orderId = newOrder.getOrderId();

            List<OrderItemRequestDTO> finalOrderItems = new ArrayList<>();
            for (ProductOrder item : orderItems) {
                finalOrderItems.add(OrderItemRequestDTO.builder()
                        .orderId(orderId)
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build());
            }

            for (OrderItemRequestDTO orderItem : finalOrderItems) {
                orderItemService.createOrderItem(orderItem);
                productService.reduceStock(orderItem.getProductId(), orderItem.getQuantity());
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
    static class ProductOrder {
        private Long productId;
        private int quantity;
    }
}
