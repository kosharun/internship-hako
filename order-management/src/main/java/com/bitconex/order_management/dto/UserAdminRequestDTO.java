package com.bitconex.order_management.dto;


import com.bitconex.order_management.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserAdminRequestDTO {
    @NotBlank(message = "Username cannot be empty!")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "Password cannot be empty!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    private String password;

    @NotBlank(message = "Email cannot be empty!")
    @Email(message = "Invalid email format!")
    private String email;

    @NotNull(message = "Role cannot be null!")
    private Role role;
}
