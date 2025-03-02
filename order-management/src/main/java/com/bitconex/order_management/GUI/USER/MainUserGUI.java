package com.bitconex.order_management.GUI.USER;

import com.bitconex.order_management.dto.OrderDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.service.OrderService;
import com.bitconex.order_management.utils.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

import static com.bitconex.order_management.utils.ConsoleUtil.print;
import static com.bitconex.order_management.utils.ConsoleUtil.printError;

@Component
public class MainUserGUI {
    private final Scanner scanner = new Scanner(System.in);
    private final OrderService orderService;
    private final SessionManager sessionManager;

    public MainUserGUI(OrderService orderService, SessionManager sessionManager) {
        this.orderService = orderService;
        this.sessionManager = sessionManager;
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

}