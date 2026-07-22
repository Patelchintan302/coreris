package com.example.coreris.repository;

import com.example.coreris.entity.Report;
import com.example.coreris.entity.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    Optional<ScanResult> findByAppointmentId(Long appointmentId);
}