package com.bitconex.order_management.service;

import com.bitconex.order_management.entity.Role;
import com.bitconex.order_management.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    @DisplayName("Should return role - getRole")
    public void testGetRole_ShouldReturnRole() {
        Role role = new Role();
        role.setName("ADMIN");
        role.setRoleId(1L);

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        Optional<Role> roleOptional = roleService.getRole("ADMIN");

        assertThat(roleOptional).isNotNull();
        assertThat(roleOptional.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should throw exception - getRole")
    public void testGetRole_ShouldThrowException() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> roleService.getRole("ADMIN"));
    }
}
