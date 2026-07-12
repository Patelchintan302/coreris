package com.example.coreris.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Receptionist {
    @Id
    private Long id;

    private String name;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;


}
