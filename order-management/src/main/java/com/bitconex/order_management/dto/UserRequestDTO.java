package com.bitconex.order_management.dto;

import com.bitconex.order_management.entity.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private LocalDate dateOfBirth;
    private AddressDTO address;
}
