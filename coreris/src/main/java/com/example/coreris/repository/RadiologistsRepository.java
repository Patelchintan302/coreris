package com.example.coreris.repository;

import com.example.coreris.entity.Radiologists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RadiologistsRepository extends JpaRepository<Radiologists, Long> {
}