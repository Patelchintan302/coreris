package com.example.coreris.repository;

import com.example.coreris.entity.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    Optional<ScanResult> findByAppointmentId(Long appointmentId);
}