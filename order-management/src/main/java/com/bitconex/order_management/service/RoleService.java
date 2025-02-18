package com.bitconex.order_management.service;

import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public Optional<Role> getRole(String roleName) {
        Optional<Role> role = Optional.ofNullable(roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role with that name does not exist!")));
        return role;
    }


}
