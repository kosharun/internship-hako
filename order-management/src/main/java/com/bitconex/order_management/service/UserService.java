package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.AddressDTO;
import com.bitconex.order_management.dto.UserAdminRequestDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.entity.User;
import static com.bitconex.order_management.mapper.DTOMapper.*;

import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.RoleRepository;
import com.bitconex.order_management.repository.UserRepository;
import static com.bitconex.order_management.utils.ConsoleUtil.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;
    private final Validator validator; // 🔹 Inject Validator



    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, DTOMapper dtoMapper, Validator validator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoMapper = dtoMapper;
        this.validator = validator;
    }

    public UserDTO createUser(@Valid UserRequestDTO userRequestDTO) {
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userRequestDTO);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<UserRequestDTO> violation : violations) {
                errorMessage.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
            }
            throw new ConstraintViolationException(errorMessage.toString(), violations);
        }
        if(userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("User with that username already exists!");
        }

        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setDateOfBirth(userRequestDTO.getDateOfBirth());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        Role role = roleRepository.findByName(userRequestDTO.getRole().getName())
                .orElseThrow(() -> new RuntimeException("Role not found!"));

        user.setRole(role);


        Address address = userRequestDTO.getAddress();
        user.setAddress(address);

        userRepository.save(user);
        printSuccess("Successfully created user: " + userRequestDTO.getUsername());
        return dtoMapper.mapToDTO(user);
    }

    public UserDTO createAdmin(@Valid UserAdminRequestDTO userAdminRequestDTO) {
        Set<ConstraintViolation<UserAdminRequestDTO>> violations = validator.validate(userAdminRequestDTO);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<UserAdminRequestDTO> violation : violations) {
                errorMessage.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
            }
            throw new ConstraintViolationException(errorMessage.toString(), violations);
        }

        if(userRepository.findByUsername(userAdminRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("User with that username already exists!");
        }

        User user = new User();
        user.setUsername(userAdminRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userAdminRequestDTO.getPassword()));
        user.setEmail(userAdminRequestDTO.getEmail());

        Role role = roleRepository.findByName(userAdminRequestDTO.getRole().getName())
                .orElseThrow(() -> new RuntimeException("Role not found!"));

        user.setRole(role);
        userRepository.save(user);

        printSuccess("Successfully created user: " + userAdminRequestDTO.getUsername());
        return dtoMapper.mapToDTO(user);
    }


    public List<UserDTO> getAllUsers() {
        List<UserDTO> userDTOS = new ArrayList<>();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            userDTOS.add(dtoMapper.mapToDTO(user));
        }

        print(userDTOS.toString());

        return userDTOS;
    }

    public UserDTO getUser(String username) {
        return dtoMapper.mapToDTO(userRepository.findByUsername(username).orElseThrow());
    }


    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        userRepository.delete(user);
        printSuccess("Successfully removed user: " + username);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with that username does not exist!"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            String role = user.getRole().getName();
            printSuccess("Successfully logged in as " + username + " with a role: " + role);
            return role;
        } else {
            throw new RuntimeException("Wrong password!");
        }
    }




}
