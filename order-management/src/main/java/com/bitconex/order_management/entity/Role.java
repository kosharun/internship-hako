package com.bitconex.order_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles") // ovo znaci da se ova klasa mapira na tabelu roles / ako se ne stavi ova anotacija onda se mapira na tabelu sa imenom klase
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id // ova anotacija znaci da je ovo primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ovaj strategy je za auto increment
    private Long roleId;

    @Column(nullable = false, unique = true)
    private String name;
}
