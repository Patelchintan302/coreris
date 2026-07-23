package com.example.coreris.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ScanResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scanDetails;
    private String imageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime capturedAt;

    @OneToOne
    @ToString.Exclude
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "technician_id", nullable = false)
    private Technician technician;

    @PrePersist
    protected void onCreate() {
        this.capturedAt = LocalDateTime.now();
    }
}
