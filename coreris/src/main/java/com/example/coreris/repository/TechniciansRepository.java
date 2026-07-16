package com.example.coreris.repository;

import com.example.coreris.entity.Technicians;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechniciansRepository extends JpaRepository<Technicians, Long> {
}