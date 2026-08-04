package com.example.coreris.controller;

import com.example.coreris.dto.ScanResultCreateDto;
import com.example.coreris.dto.ScanResultDto;
import com.example.coreris.entity.User;
import com.example.coreris.service.FileStorageService;
import com.example.coreris.service.ScanResultService;

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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ScanResultController {
    private final ScanResultService scanResultService;
    private final FileStorageService fileStorageService;

    //sp note :- accessible to any authenticated user
    @GetMapping("/appointments/{id}/scan")
    public ResponseEntity<ScanResultDto> getScanResultByAppointmentId(
            @PathVariable("id") Long appointmentId
    ){
        ScanResultDto scanResultDto= scanResultService.getScanResultByAppointmentId(appointmentId);
        return ResponseEntity.ok(scanResultDto);
    }

    //sp note :- accessible to any authenticated user
    @GetMapping("scans/{id}")
    public ResponseEntity<ScanResultDto> getScanResultById(@PathVariable Long id){
        ScanResultDto scanResultDto  = scanResultService.getScanResultById(id);
        return ResponseEntity.ok(scanResultDto);
    }

    //sp note :- accessible to only technician and admin
    @PostMapping(value = "/appointments/{id}/scan",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ScanResultDto> createScanResult(
            @PathVariable("id") Long appointmentId,
            @AuthenticationPrincipal User loggedInUser,
            @RequestParam("file") MultipartFile file,
            @ModelAttribute @Valid ScanResultCreateDto scanResultCreateDto
    ){
        Long technicianId = loggedInUser.getId();
        ScanResultDto createdScanResultDto = scanResultService.createScanResult(appointmentId, technicianId, file, scanResultCreateDto);
        return new ResponseEntity<>(createdScanResultDto, HttpStatus.CREATED);
    }

    //sp note :- accessible to only technician and admin
    @PutMapping("/appointments/{id}/scan")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ScanResultDto> updateScanResult(
            @PathVariable("id") Long appointmentId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Valid @RequestBody ScanResultCreateDto scanResultCreateDto
    ) {
        ScanResultDto updated = scanResultService.updateScanResult(appointmentId, file, scanResultCreateDto);
        return ResponseEntity.ok(updated);
    }

    //sp note :- accessible to any authenticated user
    @GetMapping("/scans/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        String contentType = request.getServletContext().getMimeType(fileName);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))  // "inline" allows the browser to render it directly (instead of downloading it as a file)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
