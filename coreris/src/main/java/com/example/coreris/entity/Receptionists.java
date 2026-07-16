package com.example.coreris.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Receptionists {
    @Id
    private Long id;

    private String name;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;


}
