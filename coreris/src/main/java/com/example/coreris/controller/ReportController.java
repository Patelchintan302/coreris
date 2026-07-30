package com.example.coreris.controller;

import com.example.coreris.dto.ReportCreateDto;
import com.example.coreris.dto.ReportDto;
import com.example.coreris.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    //sp note :- accessible to any authenticated user
    @GetMapping("/appointments/{id}/report")
    public ResponseEntity<ReportDto> getReportByAppointmentId(
            @PathVariable("id") Long appointmentId
    ){
        ReportDto ReportDto = reportService.getReportByAppointmentId(appointmentId);
        return ResponseEntity.ok(ReportDto);
    }

    //sp note :- accessible to any authenticated user
    @GetMapping("reports/{id}")
    public ResponseEntity<ReportDto> getReportById(@PathVariable Long id){
        ReportDto ReportDto = reportService.getReportById(id);
        return ResponseEntity.ok(ReportDto);
    }

    //sp note :- accessible to only radiologist and admin
    @PostMapping("/appointments/{id}/report")
    @PreAuthorize("hasRole('RADIOLOGIST')")
    public ResponseEntity<ReportDto> createReport(
            @PathVariable("id") Long appointmentId,
            @RequestParam("radiologistId") Long radiologistId,
            @Valid @RequestBody ReportCreateDto reportCreateDto
    ){
        ReportDto CreatedReportDto = reportService.createReport(appointmentId, radiologistId, reportCreateDto);
        return new ResponseEntity<>(CreatedReportDto,HttpStatus.CREATED);
    }
}
