package com.example.coreris.controller;

import com.example.coreris.dto.ScanResultCreateDto;
import com.example.coreris.dto.ScanResultDto;
import com.example.coreris.entity.User;
import com.example.coreris.service.ScanResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScanResultController {
    private final ScanResultService scanResultService;

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
    @PostMapping("/appointments/{id}/scan")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ScanResultDto> createScanResult(
            @PathVariable("id") Long appointmentId,
            @AuthenticationPrincipal User loggedInUser,
            @Valid @RequestBody ScanResultCreateDto scanResultCreateDto
    ){
        Long technicianId = loggedInUser.getId();
        ScanResultDto createdScanResultDto = scanResultService.createScanResult(appointmentId, technicianId, scanResultCreateDto);
        return new ResponseEntity<>(createdScanResultDto, HttpStatus.CREATED);
    }

}
