package com.bitconex.order_management.dto;

import com.bitconex.order_management.entity.Catalog;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    @NotNull
    private Catalog catalog;

    @NotBlank(message = "Product name cannot be empty!")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters!")
    private String name;

    @NotBlank(message = "Product description cannot be empty!")
    @Size(max = 500, message = "Description must not exceed 500 characters!")
    private String description;

    @Positive(message = "Price must be a positive number!")
    private double price;

    @NotNull(message = "Date published is required!")
    @PastOrPresent(message = "Date published cannot be in the future!")
    private LocalDate datePublished;

    @NotNull(message = "Available until date is required!")
    @Future(message = "Available until date must be in the future!")
    private LocalDate availableUntil;

    @PositiveOrZero(message = "Stock quantity cannot be negative!")
    private int stockQuantity;
}
