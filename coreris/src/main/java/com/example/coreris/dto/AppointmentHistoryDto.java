package com.example.coreris.dto;

import com.example.coreris.entity.type.ScanType;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppointmentHistoryDto {
    private Long id;
    private ScanType scanType;
    private LocalDateTime appointmentTime;
    private LocalDateTime createdAt;
    private ScanResultDto scanResult;
    private ReportDto report;
}
