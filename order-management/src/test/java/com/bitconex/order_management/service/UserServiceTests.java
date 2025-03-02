package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.UserAdminRequestDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.entity.User;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.RoleRepository;
import com.bitconex.order_management.repository.UserRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DTOMapper dtoMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserService userService;


    //CREATE USER TESTS
    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser_ShouldCreateUser() {
        // Simuliramo da username ne postoji
        when(userRepository.findByUsername("runha")).thenReturn(Optional.empty());

        // Simuliramo da rola postoji
        Role role = Role.builder().name("USER").build();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

        // Simuliramo hashiranje lozinke
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        // Simuliramo UserRequestDTO
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .username("runha")
                .email("runha@gmail.com")
                .password("password")
                .role(role)
                .build();

        // Simuliramo da `save()` vraća spremljenog korisnika
        User savedUser = User.builder()
                .username("runha")
                .email("runha@gmail.com")
                .password("hashedPassword")
                .role(role)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO expectedUserDTO = UserDTO.builder()
                .username("runha")
                .email("runha@gmail.com")
                .build();

        when(dtoMapper.mapToDTO(any(User.class))).thenReturn(expectedUserDTO);

        // Pozivamo metodu
        UserDTO userDTO = userService.createUser(userRequestDTO);

        // Provjera
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getUsername()).isEqualTo("runha");
        assertThat(userDTO.getEmail()).isEqualTo("runha@gmail.com");
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void testCreateUser_ShouldThrowException_WhenUsernameExists() {
        // Simuliramo da username već postoji
        when(userRepository.findByUsername("runha")).thenReturn(Optional.of(new User()));

        // Simuliramo UserRequestDTO
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .username("runha")
                .email("newuser@gmail.com")
                .password("password123")
                .build();

        // Očekujemo RuntimeException
        assertThrows(RuntimeException.class, () -> userService.createUser(userRequestDTO));

        // Provjeravamo da `save()` nikad nije pozvan
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when role does not exist")
    void testCreateUser_ShouldThrowException_WhenRoleDoesntExist() {
        // Simuliramo da username ne postoji
        when(userRepository.findByUsername("runha")).thenReturn(Optional.empty());

        // Simuliramo da rola ne postoji
        when(roleRepository.findByName("BUYER")).thenReturn(Optional.empty());

        // Simuliramo UserRequestDTO sa nepostojećom rolom
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .username("runha")
                .email("newuser@gmail.com")
                .password("password123")
                .role(Role.builder().name("BUYER").build())
                .build();

        // Očekujemo RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> userService.createUser(userRequestDTO));

        // Provjera tačne poruke greške
        assertThat(exception.getMessage()).isEqualTo("Role not found!");

        // Provjeravamo da `save()` nikad nije pozvan
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should create admin successfully")
    void testCreateAdmin_ShouldCreateAdmin() {
        when(userRepository.findByUsername("adminUser")).thenReturn(Optional.empty());

        Role adminRole = Role.builder().name("ADMIN").build();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));

        when(passwordEncoder.encode("adminPass"))
                .thenReturn("hashedAdminPass");

        UserAdminRequestDTO adminRequestDTO = UserAdminRequestDTO.builder()
                .username("adminUser")
                .email("admin@gmail.com")
                .password("adminPass")
                .role(adminRole)
                .build();

        User savedAdmin = User.builder()
                .username("adminUser")
                .email("admin@gmail.com")
                .password("hashedAdminPass")
                .role(adminRole)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedAdmin);

        when(dtoMapper.mapToDTO(any(User.class))).thenReturn(UserDTO.builder()
                .username("adminUser").email("admin@gmail.com").build());

        UserDTO adminDTO = userService.createAdmin(adminRequestDTO);


        assertThat(adminDTO).isNotNull();
        assertThat(adminDTO.getUsername()).isEqualTo("adminUser");
        assertThat(adminDTO.getEmail()).isEqualTo("admin@gmail.com");
    }

    @Test
    @DisplayName("Should fail when username already exists")
    void testCreateAdmin_ShouldFailWhenUsernameExists() {
        User existingUser = User.builder().username("adminUser").build();
        when(userRepository.findByUsername("adminUser")).thenReturn(Optional.of(existingUser));
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        UserAdminRequestDTO adminRequestDTO = UserAdminRequestDTO.builder()
                .username("adminUser")
                .email("admin@gmail.com")
                .password("adminPass")
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.createAdmin(adminRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("User with that username already exists!");
    }

    @Test
    @DisplayName("Should fail when role is not found")
    void testCreateAdmin_ShouldFailWhenRoleNotFound() {
        when(userRepository.findByUsername("adminUser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        UserAdminRequestDTO adminRequestDTO = UserAdminRequestDTO.builder()
                .username("adminUser")
                .email("admin@gmail.com")
                .password("adminPass")
                .role(Role.builder().name("ADMIN").build())
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.createAdmin(adminRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("Role not found!");
    }

    //READING USERS TESTS
    @Test
    @DisplayName("Should return List of UserDTOs")
    void testGetAllUsers_ShouldReturnListOfUserDTOs() {

        User user1 = User.builder()
                .username("runha")
                .email("newuser@gmail.com")
                .password("password123")
                .role(Role.builder().name("USER").build())
                .build();
        User user2 = User.builder()
                .username("runha2")
                .email("newuser@gmail.com")
                .password("password1234")
                .role(Role.builder().name("USER").build())
                .build();

        when(dtoMapper.mapToDTO(user1)).thenReturn(UserDTO.builder()
                .username("runha")
                .build());

        List<User> users = List.of(user1, user2);

        //Simulating findAll method
        when(userRepository.findAll()).thenReturn(users);

        //Calling the method
        List<UserDTO> userDTOS = userService.getAllUsers();

        assertThat(userDTOS).isNotEmpty();
    }

    @Test
    @DisplayName("Should return UserDTO")
    void testGetUser_ShouldReturnUserDTO() {

        User user = User.builder()
                .username("runha")
                .email("newuser@gmail.com")
                .password("password123")
                .role(Role.builder().name("USER").build())
                .build();


        //Simulating findByUsername method
        when(userRepository.findByUsername("runha")).thenReturn(Optional.ofNullable(user));
        when(dtoMapper.mapToDTO(any(User.class))).thenReturn(UserDTO.builder().username("runha").build());

        //Calling the method
        UserDTO userDTO = userService.getUser("runha");

        assertThat(userDTO).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception is user not found")
    void testGetUser_ShouldThrowException_WhenNoUser() {
        //Simulating findByUsername method
        when(userRepository.findByUsername("runha")).thenReturn(Optional.empty());

        assertThrows(Throwable.class, () -> userService.getUser("runha"));
    }


    //DELETING USERS TESTS
    @Test
    @DisplayName("Should delete a user")
    void testDeleteUser_ShouldDeleteUser() {

        User user = User.builder()
                .username("runha")
                .email("newuser@gmail.com")
                .password("password123")
                .role(Role.builder().name("USER").build())
                .build();

        when(userRepository.findByUsername("runha")).thenReturn(Optional.of(user));

        //Calling the method
        userService.deleteUser("runha");

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Should throw exception when deleting a user")
    void testDeleteUser_ShouldThrowException_WhenDeleteUser() {
        User user = User.builder()
                .username("runha")
                .email("newuser@gmail.com")
                .password("password123")
                .role(Role.builder().name("USER").build())
                .build();

        when(userRepository.findByUsername("runha2")).thenReturn(Optional.empty());

        //Calling the method
        assertThrows(Throwable.class, () -> userService.deleteUser("runha"));
    }

    //LOGIN USERS TESTS
    @Test
    @DisplayName("Should log in a user")
    void testLogin_ShouldLoginUser() {
        User user = User.builder()
                .username("runha")
                .email("newuser@gmail.com")
                .password("password123")
                .role(Role.builder().name("USER").build())
                .build();
        when(userRepository.findByUsername("runha")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "password123")).thenReturn(true);

        assertThat(userService.login("runha", "password123")).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception when username does not exist")
    void testLogin_ShouldThrowException_WhenUsernameNotExist() {
        when(userRepository.findByUsername("runha")).thenReturn(Optional.empty());
        assertThrows(Throwable.class, () -> userService.login("runha", "password123"));
    }

    @Test
    @DisplayName("Should throw exception when wrong password")
    void testLogin_ShouldThrowException_WhenWrongPassword() {
        User user = User.builder()
                .username("runha")
                .email("newuser@gmail.com")
                .password("password123")
                .role(Role.builder().name("USER").build())
                .build();
        when(userRepository.findByUsername("runha")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "password123")).thenReturn(false);

        assertThrows(Throwable.class, () -> userService.login("runha", "password123"));
    }

}

