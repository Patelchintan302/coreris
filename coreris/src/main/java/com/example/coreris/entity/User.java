package com.example.coreris.entity;

import com.example.coreris.entity.type.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @OneToOne(mappedBy = "user",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private Receptionist receptionist;

    @OneToOne(mappedBy = "user",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private Technician technician;

    @OneToOne(mappedBy = "user",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private Radiologist radiologist;


}
