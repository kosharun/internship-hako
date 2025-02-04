package com.bitconex.order_management.GUI;

import static com.bitconex.order_management.utils.ConsoleUtil.*;

import com.bitconex.order_management.dto.AddressDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.repository.AddressRepository;
import com.bitconex.order_management.repository.RoleRepository;
import com.bitconex.order_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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

            print("\n🔹 ADMIN PANEL 🔹");
            print("1. Create New User");
            print("2. View All Users");
            print("3. Remove An User");
            print("4. Exit");

            print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
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
                    String roleName = scanner.nextLine();
                    Optional<Role> role = roleRepository.findByName(roleName);

                    print("Enter Date Of Birth (yyyy-mm-dd): ");
                    String dateOfBirth = scanner.nextLine();

                    print("Enter user's address.");
                    print("Enter a Street:");
                    String street = scanner.nextLine();

                    print("Enter a Zipcode:");
                    String zipCode = scanner.nextLine();

                    print("Enter the Place name:");
                    String placeName = scanner.nextLine();

                    print("Enter the State name:");
                    String stateName = scanner.nextLine();

                    AddressDTO address = new AddressDTO();
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
                            .dateOfBirth(LocalDate.parse(dateOfBirth))
                            .build();

                    userService.createUser(userRequestDTO);

                    break;
                case 2:
                    print("2");
                    break;
                case 3:
                    print("3");
                    break;
                case 4:
                    System.out.println("Exiting Admin Panel...");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }
}
