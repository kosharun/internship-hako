package com.bitconex.order_management;

import com.bitconex.order_management.GUI.MainMenu;
import com.bitconex.order_management.dto.AddressDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.repository.RoleRepository;
import com.bitconex.order_management.repository.UserRepository;
import com.bitconex.order_management.service.UserService;
import com.bitconex.order_management.utils.SessionManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import java.time.LocalDate;
import java.util.Optional;

import static com.bitconex.order_management.utils.ConsoleUtil.*;

@SpringBootApplication
@EntityScan("com.bitconex.order_management.entity")
@EnableJpaRepositories("com.bitconex.order_management.repository")
public class OrderManagementApplication {

	private final MainMenu mainMenu;

	public OrderManagementApplication(MainMenu mainMenu) {
		this.mainMenu = mainMenu;
	}

	public static void main(String[] args) {
		printSuccess("Application started successfully!");
		SpringApplication.run(OrderManagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> {
			mainMenu.start();
			
		};
	}
}
