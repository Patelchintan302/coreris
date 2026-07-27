package com.example.coreris.repository;

import com.example.coreris.entity.Appointment;
import com.example.coreris.entity.type.StatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByStatus(StatusType status, Pageable pageable);
}