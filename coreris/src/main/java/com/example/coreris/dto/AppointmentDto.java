package com.example.coreris.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private long id;
    private LocalDateTime appointmentTime;
    private PatientDto patientDto;
}
