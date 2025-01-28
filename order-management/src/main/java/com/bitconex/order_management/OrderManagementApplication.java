package com.bitconex.order_management;

import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.entity.User;
import com.bitconex.order_management.repository.RoleRepository;
import com.bitconex.order_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OrderManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderManagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(UserRepository userRepository, RoleRepository roleRepository) {
		return args -> {
			/*// Kreiranje i čuvanje role
			Role adminRole = new Role();
			adminRole.setName("ADMIN");
			roleRepository.save(adminRole);

			// Kreiranje i čuvanje korisnika
			User user = new User();
			user.setUsername("john_doe");
			user.setPassword("securepassword");
			user.setEmail("john@example.com");
			user.setRole(adminRole);

			userRepository.save(user);

			// Ispis svih korisnika
			System.out.println("Lista svih korisnika:");
			userRepository.findAll().forEach(u -> System.out.println(u.getUsername() + " - " + u.getRole().getName()));*/
		};
	}
}
