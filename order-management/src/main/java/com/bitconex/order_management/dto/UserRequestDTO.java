package com.bitconex.order_management.dto;

import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
public class UserRequestDTO {
    @NotBlank(message = "Username cannot be empty!")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "Password cannot be empty!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    private String password;

    @NotBlank(message = "Email cannot be empty!")
    @Email(message = "Invalid email format!")
    private String email;

    @NotBlank(message = "First Name cannot be empty!")
    private String firstName;

    @NotBlank(message = "Last Name cannot be empty!")
    private String lastName;

    @NotNull(message = "Role cannot be null!")
    private Role role;

    @Past(message = "Date of Birth must be in the past!")
    private LocalDate dateOfBirth;

    @NotNull (message = "Address is required")
    private Address address;
}
