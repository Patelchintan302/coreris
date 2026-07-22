package com.example.coreris.controller;

import com.example.coreris.dto.ScanResultCreateDto;
import com.example.coreris.dto.ScanResultDto;
import com.example.coreris.service.ScanResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScanResultController {
    private final ScanResultService scanResultService;

    @PostMapping("/appointments/{id}/scan")
    public ResponseEntity<ScanResultDto> createScanResult(
            @PathVariable("id") Long appointmentId,
            @RequestParam("technicianId") Long technicianId,
            @Valid @RequestBody ScanResultCreateDto scanResultCreateDto
            ){
        ScanResultDto createdScanResultDto = scanResultService.createScanResult(appointmentId, technicianId, scanResultCreateDto);
        return new ResponseEntity<>(createdScanResultDto, HttpStatus.CREATED);
    }

    @GetMapping("/appointments/{id}/scan")
    public ResponseEntity<ScanResultDto> getScanResultByAppointmentId(
            @PathVariable("id") Long appointmentId
    ){
        ScanResultDto scanResultDto= scanResultService.getScanResultByAppointmentId(appointmentId);
        return ResponseEntity.ok(scanResultDto);
    }

    @GetMapping("scans/{id}")
    public ResponseEntity<ScanResultDto> getScanResultById(@PathVariable Long id){
        ScanResultDto scanResultDto  = scanResultService.getScanResultById(id);
        return ResponseEntity.ok(scanResultDto);
    }

}
