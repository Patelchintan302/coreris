package com.example.coreris.controller;

import com.example.coreris.dto.TechnicianDto;
import com.example.coreris.service.TechnicianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/technicians")
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianService technicianService;

    @GetMapping
    public ResponseEntity<List<TechnicianDto>> getAllTechnicians() {
        return ResponseEntity.ok(technicianService.getAllTechnicians());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TechnicianDto> updateTechnician(@PathVariable Long id, @RequestBody TechnicianDto dto) {
        return ResponseEntity.ok(technicianService.updateTechnician(id, dto));
    }
}