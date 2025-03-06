package com.bitconex.order_management.GUI.ADMIN;

import com.bitconex.order_management.GUI.MainMenu;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Scanner;

import static com.bitconex.order_management.utils.ConsoleUtil.print;
import static com.bitconex.order_management.utils.ConsoleUtil.printError;

@Component
public class MainAdminGUI {
    private final Scanner scanner = new Scanner(System.in);
    private final AdministrationGUI administrationGUI;
    private final ProductCatalogGUI productCatalogGUI;
    private final MainMenu mainMenu;
    private final OrderAdministrationGUI orderAdministrationGUI;

    public MainAdminGUI(AdministrationGUI administrationGUI, ProductCatalogGUI productCatalogGUI, @Lazy MainMenu mainMenu, OrderAdministrationGUI orderAdministrationGUI) {
        this.administrationGUI = administrationGUI;
        this.productCatalogGUI = productCatalogGUI;
        this.mainMenu = mainMenu;
        this.orderAdministrationGUI = orderAdministrationGUI;
    }


    public void startAdminConsole() {
        while (true) {

            int choice;
            while (true) {
                try {
                    print("\n🔹 ADMIN PANEL 🔹");
                    print("1. User Administration");
                    print("2. Product Catalog");
                    print("3. Order Administration");
                    print("4. Export list of all orders in CSV");
                    print("5. Exit");

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
                        administrationGUI.startUserAdminConsole();
                    } catch (Exception e) {
                        printError("Error creating user - " + e.getMessage());
                    }

                    break;
                case 2:
                    try {
                        productCatalogGUI.startProductCatalogConsole();
                    } catch (Exception e) {
                        printError("Error fetching users - " + e.getMessage());
                    }
                    break;
                case 3:
                    orderAdministrationGUI.startOrderAdminConsole();
                    break;
                case 4:

                    break;
                case 5:
                    print("Exiting Admin Panel...");
                    mainMenu.start();
                    return;
                default:
                    printError("Invalid choice. Try again.");
            }
        }
    }

}
