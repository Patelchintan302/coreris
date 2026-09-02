package com.example.coreris.controller;

import com.example.coreris.dto.ReportCreateDto;
import com.example.coreris.dto.ReportDto;
import com.example.coreris.entity.User;
import com.example.coreris.service.FileStorageService;
import com.example.coreris.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final FileStorageService fileStorageService;

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
            @AuthenticationPrincipal User loggedInUser,
            @Valid @RequestBody ReportCreateDto reportCreateDto
    ){
        Long radiologistId = loggedInUser.getId();
        ReportDto CreatedReportDto = reportService.createReport(appointmentId, radiologistId, reportCreateDto);
        return new ResponseEntity<>(CreatedReportDto,HttpStatus.CREATED);
    }

    //sp note :- accessible to only radiologist and admin
    @PutMapping("/appointments/{id}/report")
    @PreAuthorize("hasRole('RADIOLOGIST')")
    public ResponseEntity<ReportDto> updateReport(
            @PathVariable("id") Long appointmentId,
            @Valid @RequestBody ReportCreateDto reportCreateDto
    ) {
        ReportDto updated = reportService.updateReport(appointmentId, reportCreateDto);
        return ResponseEntity.ok(updated);
    }

    //sp note :- accessible to any authenticated user
    @GetMapping(value = "/reports/download/{fileName:.+}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> downloadReportPdf(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        String contentType = request.getServletContext().getMimeType(fileName);
        if (contentType == null) {
            contentType = "application/pdf";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
