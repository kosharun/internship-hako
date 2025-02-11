package com.bitconex.order_management.GUI;

import static com.bitconex.order_management.utils.ConsoleUtil.*;

import com.bitconex.order_management.dto.AddressDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.repository.AddressRepository;
import com.bitconex.order_management.repository.RoleRepository;
import com.bitconex.order_management.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Administration {
    private final Scanner scanner = new Scanner(System.in);
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;


    public Administration(RoleRepository roleRepository, AddressRepository addressRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    public void startAdminConsole() {
        while (true) {

            int choice;
            while (true) {
                try {
                    print("\n🔹 ADMIN PANEL 🔹");
                    print("1. Create New User");
                    print("2. View All Users");
                    print("3. Remove An User");
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
                        createUser();
                    } catch (Exception e) {
                        printError("Error creating user - " + e.getMessage());
                    }

                    break;
                case 2:
                    try {
                        getAllUsers();
                    } catch (Exception e) {
                        printError("Error fetching users - " + e.getMessage());
                    }
                    break;
                case 3:
                    removeUser();
                    break;
                case 4:
                    print("Exiting Admin Panel...");
                    return;
                default:
                    printError("Invalid choice. Try again.");
            }
        }
    }

    void createUser() throws Exception {
        print("Enter a Username: ");
        String username = scanner.nextLine();

        print("Enter a Password: ");
        String password = scanner.nextLine();

        print("Enter an Email: ");
        String email = scanner.nextLine();

        print("Enter First Name: ");
        String firstName = scanner.nextLine();

        print("Enter Last Name: ");
        String lastName = scanner.nextLine();

        print("Enter a Role (ADMIN, USER): ");
        String roleName = scanner.nextLine().toUpperCase();
        Optional<Role> role = roleRepository.findByName(roleName);              //MOZDA DA SE NE KORISTI ROLEREPOSITORY DIREKT

        if(role.isEmpty()) {
            throw new Exception("Role not found!");
        }

        print("Enter Date Of Birth (yyyy-mm-dd): ");
        String dateOfBirthStr = scanner.nextLine();

        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(dateOfBirthStr);
        } catch (Exception e) {
            printError("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        print("Enter user's address.");
        print("Enter a Street:");
        String street = scanner.nextLine();

        print("Enter a Zipcode:");
        String zipCode = scanner.nextLine();

        print("Enter the Place name:");
        String placeName = scanner.nextLine();

        print("Enter the State name:");
        String stateName = scanner.nextLine();

        Address address = new Address();
        address.setStreet(street);
        address.setZipCode(zipCode);
        address.setPlaceName(placeName);
        address.setStateName(stateName);


        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .username(username)
                .password(password)
                .role(role.get())
                .address(address)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .build();

        userService.createUser(userRequestDTO);
    }

    void getAllUsers() {

        // Konfigurisanje ObjectMapper-a
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // support Java 8 date/time API
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Lijep format

        try {
            List<UserDTO> users = userService.getAllUsers();
            String jsonOutput = objectMapper.writeValueAsString(users);
            print(jsonOutput);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving users: " + e.getMessage());
        }

    }

    void removeUser() {
        print("Username of the user you want to remove:");
        String username = scanner.nextLine();
        try {
           userService.deleteUser(username);
        } catch (Exception e) {
            printError("Error removing user: " + e.getMessage());
        }
    }
}
