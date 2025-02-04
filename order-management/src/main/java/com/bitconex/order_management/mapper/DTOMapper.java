package com.bitconex.order_management.mapper;

import com.bitconex.order_management.dto.AddressDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.User;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {


    public static UserDTO mapToDTO(User user) {
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
