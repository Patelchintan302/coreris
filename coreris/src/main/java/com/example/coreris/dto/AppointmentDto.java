package com.example.coreris.dto;

import com.example.coreris.entity.type.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AppointmentDto {
    private Long id;
    private LocalDateTime appointmentTime;
    private LocalDateTime createdAt;
    private PatientDto patient;
    private StatusType status;
}
