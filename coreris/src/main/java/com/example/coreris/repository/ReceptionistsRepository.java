package com.example.coreris.repository;

import com.example.coreris.entity.Receptionists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionistsRepository extends JpaRepository<Receptionists, Long> {
}