package com.example.coreris.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Radiologist {
    @Id
    private Long id;

    private String name;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "radiologist",cascade = {CascadeType.REMOVE},orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Report> report = new ArrayList<>();

}
