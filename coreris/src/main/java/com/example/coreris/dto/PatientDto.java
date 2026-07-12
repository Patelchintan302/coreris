package com.example.coreris.dto;

import com.example.coreris.entity.type.BloodGroupType;
import lombok.Data;

@Data
public class PatientDto {
    private long id;
    private String name;
    private String email;
    private Long mobileNo;
    private BloodGroupType bloodGroup;
}
