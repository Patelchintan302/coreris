package com.example.coreris.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Long id;
    private String finding;
//    private String diagnosis;
    private LocalDateTime createdAt;
    private RadiologistDto radiologist;
    private Long appointmentId;
}
