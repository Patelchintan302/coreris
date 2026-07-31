package com.example.coreris.entity;

import com.example.coreris.entity.type.ScanType;
import com.example.coreris.entity.type.StatusType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ScanType scanType;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appointmentTime;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties("appointments")
    private Patient patient;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private ScanResult scanResult;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Report report;

    @Enumerated(EnumType.STRING)
    private StatusType status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if(status == null){
            status = StatusType.PENDING;
        }
    }


}
