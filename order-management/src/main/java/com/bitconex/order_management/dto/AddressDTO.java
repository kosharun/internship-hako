package com.bitconex.order_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AddressDTO {
    private String street;
    private String zipCode;
    private String placeName;
    private String stateName;
}
