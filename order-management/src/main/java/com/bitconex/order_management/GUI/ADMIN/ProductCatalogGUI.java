package com.bitconex.order_management.GUI.ADMIN;

import com.bitconex.order_management.dto.ProductRequestDTO;

import com.bitconex.order_management.service.ProductService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Scanner;

import static com.bitconex.order_management.utils.ConsoleUtil.print;
import static com.bitconex.order_management.utils.ConsoleUtil.printError;

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
                    } catch (Exception e) {
                        printError("Error fetching products - " + e.getMessage());
                    }
                    break;
                case 3:
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

}
