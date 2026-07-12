package com.example.coreris.repository;

import com.example.coreris.entity.Radiologist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RadiologiestRepository extends JpaRepository<Radiologist, Long> {
}