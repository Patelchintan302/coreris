package com.example.coreris.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCreateDto {
    @NotNull(message = "Radiologist ID is required")
    private Long radiologistId;
    @NotBlank(message = "Finding is Required")
    private String finding;

//    @NotBlank(message = "Diagnosis is Required")
//    private String diagnosis;
}
