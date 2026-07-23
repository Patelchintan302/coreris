package com.example.coreris.dto;

import com.example.coreris.entity.type.BloodGroupType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientHistoryDto {
    private Long id;
    private String name;
    private String email;
    private Long mobileNo;
    private BloodGroupType bloodGroup;
    private LocalDate dob;
    private String gender;
    private List<AppointmentHistoryDto>  appointments;
}
