package com.example.coreris.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppointmentHistoryDto {
    private Long id;
    private LocalDateTime appointmentTime;
    private LocalDateTime createdAt;
    private ScanResultDto scanResult;
    private ReportDto report;
}
