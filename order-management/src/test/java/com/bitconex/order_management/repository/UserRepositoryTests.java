package com.bitconex.order_management.repository;

import com.bitconex.order_management.entity.User;
import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.entity.Address;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest // Fokusira test samo na JPA sloj
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) // Koristi H2 bazu
@SpringJUnitConfig // Ne učitava cijelu aplikaciju, samo potrebne komponente
@ExtendWith(SpringExtension.class)
public class UserRepositoryTests {

    private final UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    public UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User user;

    @BeforeEach
    void setup() {
        Role role = Role.builder().name("ADMIN").build();
        Optional<Role> optionalRole = Optional.of(roleRepository.save(role));

        user = User.builder()
                .firstName("Runha")
                .lastName("Kos")
                .email("runha@gmail.com")
                .dateOfBirth(LocalDate.parse("1992-12-12"))
                .username("runha")
                .password("password")
                .address(Address.builder()
                        .street("ulica")
                        .zipCode("12345")
                        .placeName("nicaze")
                        .stateName("bih")
                        .build()
                )
                .role(optionalRole.get())
                .build();
    }

    @Test
    public void UserRepository_IsNotNull() {
        Assertions.assertThat(userRepository).isNotNull();
    }

    @Test
    public void UserRepository_Save_ReturnSavedUser() {
        // Act
        User savedUser = userRepository.save(user);

        // Assert
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getUserId()).isGreaterThan(0);
    }
}
