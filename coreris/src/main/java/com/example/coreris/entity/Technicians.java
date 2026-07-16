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
public class Technicians {
    @Id
    private long id;

    private String name;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "technician",cascade = {CascadeType.REMOVE},orphanRemoval = true,fetch = FetchType.LAZY)
    private List<ScanResult> scanResults = new ArrayList<>();
//
//    @OneToMany
//    private List<Appointment> appointments = new ArrayList<>();
}
