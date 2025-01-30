package com.bitconex.order_management;

import com.bitconex.order_management.dto.AddressDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.repository.RoleRepository;
import com.bitconex.order_management.repository.UserRepository;
import com.bitconex.order_management.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootApplication
public class OrderManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderManagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
		return args -> {

			/*// Provjera da li postoji rola "USER" u bazi
			Optional<Role> userRole = roleRepository.findByName("USER");
			if (userRole.isEmpty()) {
				Role role = new Role();
				role.setName("USER");
				roleRepository.save(role);
				System.out.println("✅ Role 'USER' created!");
				userRole = Optional.of(role);
			}

			// Kreiranje novog korisnika
			UserRequestDTO newUser = new UserRequestDTO();
			newUser.setUsername("marko123");
			newUser.setPassword("securepassword");
			newUser.setEmail("marko@example.com");
			newUser.setFirstName("Marko");
			newUser.setLastName("Marković");
			newUser.setDateOfBirth(LocalDate.of(1995, 4, 10));
			newUser.setRole(userRole.get()); // Postavljanje role

			// Dodavanje adrese korisniku
			AddressDTO addressDTO = new AddressDTO();
			addressDTO.setStreet("789 Oak St");
			addressDTO.setZipCode("30003");
			addressDTO.setPlaceName("Los Angeles");
			addressDTO.setStateName("CA");
			newUser.setAddress(addressDTO);

			try {
				UserDTO createdUser = userService.createUser(newUser);
				System.out.println("✅ User created: " + createdUser);
			} catch (RuntimeException e) {
				System.out.println("⚠️ Error creating user: " + e.getMessage());
			}*/

			// Ispis svih korisnika u bazi
			System.out.println(userService.getUser("marko123").toString());
		};
	}
}
