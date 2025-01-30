package com.bitconex.order_management.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String street;
    private String zipCode;
    private String placeName;
    private String stateName;

}
