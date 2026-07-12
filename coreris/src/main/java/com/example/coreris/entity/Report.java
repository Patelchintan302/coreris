package com.example.coreris.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Report {
    @Id
    private long id;

    private String scan;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private ScanResult scanResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Radiologist radiologist;
}
