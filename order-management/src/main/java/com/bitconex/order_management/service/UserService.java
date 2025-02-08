package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.AddressDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.entity.User;
import static com.bitconex.order_management.mapper.DTOMapper.*;

import com.bitconex.order_management.repository.RoleRepository;
import com.bitconex.order_management.repository.UserRepository;
import static com.bitconex.order_management.utils.ConsoleUtil.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(UserRequestDTO userRequestDTO) {

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
        return mapToDTO(user);
    }


    public List<UserDTO> getAllUsers() {
        List<UserDTO> userDTOS = new ArrayList<>();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            userDTOS.add(mapToDTO(user));
        }

        return userDTOS;
    }

    public UserDTO getUser(String username) {
        return mapToDTO(userRepository.findByUsername(username).orElseThrow());
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
            printSuccess("Successfully logged in as" + username + " with a role: " + role);
            return role;
        } else {
            throw new RuntimeException("Wrong password!");
        }
    }




}
