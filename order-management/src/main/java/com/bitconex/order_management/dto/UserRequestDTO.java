package com.bitconex.order_management.dto;

import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
public class UserRequestDTO {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private LocalDate dateOfBirth;
    private Address address;
}
