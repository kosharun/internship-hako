package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.AddressDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.entity.User;
import com.bitconex.order_management.repository.AddressRepository;
import com.bitconex.order_management.repository.RoleRepository;
import com.bitconex.order_management.repository.UserRepository;
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


        AddressDTO addressDTO = userRequestDTO.getAddress();
        Address address = new Address();
        address.setStreet(addressDTO.getStreet());
        address.setZipCode(addressDTO.getZipCode());
        address.setPlaceName(addressDTO.getPlaceName());
        address.setStateName(addressDTO.getStateName());

        user.setAddress(address);

        userRepository.save(user);
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
    }




    public UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRole(user.getRole());
        userDTO.setDateOfBirth(user.getDateOfBirth());

        AddressDTO addressDTO = new AddressDTO();
        Address userAddress = user.getAddress();
        addressDTO.setStreet(userAddress.getStreet());
        addressDTO.setZipCode(userAddress.getZipCode());
        addressDTO.setPlaceName(userAddress.getPlaceName());
        addressDTO.setStateName(userAddress.getStateName());

        userDTO.setAddress(addressDTO);
        return userDTO;

    }
}
