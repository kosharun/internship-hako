package com.bitconex.order_management.dto;

import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.Role;
import lombok.*;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private LocalDate dateOfBirth;
    private Address address;



}
