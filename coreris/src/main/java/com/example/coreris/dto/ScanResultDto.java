package com.example.coreris.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScanResultDto {
    private Long id;
    private String scanDetails;
    private String imageUrl;
    private LocalDateTime capturedAt;
    private TechnicianDto technician;
    private Long adminId;
    private Long appointmentId;
}
