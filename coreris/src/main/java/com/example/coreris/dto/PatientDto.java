package com.example.coreris.dto;

import com.example.coreris.entity.type.BloodGroupType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientDto {
    private Long id;
    private String name;
    private String email;
    private Long mobileNo;
    private BloodGroupType bloodGroup;
    private LocalDate dob;
    private String gender;
}
