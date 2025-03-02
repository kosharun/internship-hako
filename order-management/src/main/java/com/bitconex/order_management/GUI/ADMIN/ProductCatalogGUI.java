package com.bitconex.order_management.GUI.ADMIN;

import com.bitconex.order_management.dto.OrderItemDTO;
import com.bitconex.order_management.dto.ProductRequestDTO;

import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.service.ProductService;
import com.github.freva.asciitable.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.bitconex.order_management.utils.ConsoleUtil.*;


@Component
public class ProductCatalogGUI {
    private final Scanner scanner = new Scanner(System.in);
    private final ProductService productService;

    public ProductCatalogGUI(ProductService productService) {
        this.productService = productService;
    }

    public void startProductCatalogConsole() {
        while (true) {

            int choice;
            while (true) {
                try {

                    print("\n🔹 PRODUCT CATALOG 🔹");
                    print("1. Create New Product");
                    print("2. View All Products");
                    print("3. Remove A Product");
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
                        createProduct();
                    } catch (Exception e) {
                        printError("Error creating product - " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        getAllProducts();
                    } catch (Exception e) {
                        printError("Error fetching products - " + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        removeProduct();
                    } catch (Exception e) {
                        printError("Error removing product - " + e.getMessage());
                    }
                    break;
                case 4:
                    print("Exiting Product Catalog...");
                    return;
                default:
                    printError("Invalid choice. Try again.");
            }
        }
    }

    void createProduct() {
        try {
            print("Enter name of the Product: ");
            String name = scanner.nextLine();

            print("Enter a description: ");
            String description = scanner.nextLine();

            print("Enter a price: ");
            double price = scanner.nextDouble();
            scanner.nextLine();

            print("Enter Date until the product is available (yyyy-mm-dd): ");
            String availableUntilStr = scanner.nextLine();

            LocalDate availableUntil;
            try {
                availableUntil = LocalDate.parse(availableUntilStr);
            } catch (Exception e) {
                printError("Invalid date format. Use yyyy-MM-dd.");
                return;
            }

            print("Enter Stock Quantity: ");
            int stockQuantity = scanner.nextInt();
            scanner.nextLine();

            ProductRequestDTO productRequestDTO = ProductRequestDTO.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .availableUntil(availableUntil)
                    .stockQuantity(stockQuantity)
                    .build();

            productService.createProduct(productRequestDTO);
        } catch (Exception e) {
            printError("Invalid date format. Use yyyy-MM-dd.");
        }
    }

    public void getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            printInfo("No available products!");
            return;
        }

        String table = AsciiTable.getTable(products, Arrays.asList(
                new Column()
                        .header("ID")
                        .headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER)
                        .with(product -> String.valueOf(product.getProductId())),
                new Column()
                        .header("Catalog")
                        .headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.LEFT)
                        .with(product -> product.getCatalog() != null ? product.getCatalog().getName() : "N/A"),
                new Column()
                        .header("Name")
                        .headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.LEFT)
                        .with(Product::getName),
                new Column()
                        .header("Description")
                        .headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.LEFT)
                        .with(product -> product.getDescription() != null ? product.getDescription() : "N/A"),
                new Column()
                        .header("Price")
                        .headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.RIGHT)
                        .with(product -> String.format("%.2f", product.getPrice())),
                new Column()
                        .header("Published")
                        .headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER)
                        .with(product -> product.getDatePublished() != null ? product.getDatePublished().toString() : "N/A"),
                new Column()
                        .header("Available Until")
                        .headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER)
                        .with(product -> product.getAvailableUntil() != null ? product.getAvailableUntil().toString() : "Indefinite"),
                new Column()
                        .header("Stock")
                        .headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER)
                        .with(product -> String.valueOf(product.getStockQuantity()))
        ));

        print(table);
    }

    void removeProduct() {
        getAllProducts();

        print("Type in the ID of the product you want to remove: ");
        Long Id = scanner.nextLong();
        scanner.nextLine();

        List<OrderItemDTO> orderItemDTOS = productService.findOrderItemsRelatedToProduct(Id);

        if (!orderItemDTOS.isEmpty()) {
            printInfo("This product is associated with the following order items:");

            String table = AsciiTable.getTable(orderItemDTOS, Arrays.asList(
                    new Column()
                            .header("Order ID")
                            .headerAlign(HorizontalAlign.CENTER)
                            .dataAlign(HorizontalAlign.CENTER)
                            .with(orderItem -> String.valueOf(orderItem.getOrderId())),
                    new Column()
                            .header("Product ID")
                            .headerAlign(HorizontalAlign.CENTER)
                            .dataAlign(HorizontalAlign.CENTER)
                            .with(orderItem -> String.valueOf(orderItem.getProductId())),
                    new Column()
                            .header("Quantity")
                            .headerAlign(HorizontalAlign.CENTER)
                            .dataAlign(HorizontalAlign.RIGHT)
                            .with(orderItem -> String.valueOf(orderItem.getQuantity()))
            ));

            print(table);

            print("Are you sure you want to remove this product? (yes/no)");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!confirmation.equals("yes")) {
                printInfo("Product removal canceled.");
                return;
            }
        }

        productService.removeProduct(Id);
        printSuccess("Product removed successfully.");
    }





}
