package com.example.coreris.controller;

import com.example.coreris.dto.RadiologistDto;
import com.example.coreris.service.RadiologistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/radiologists")
@RequiredArgsConstructor
public class RadiologistController {

    private final RadiologistService radiologistService;

    @GetMapping
    public ResponseEntity<List<RadiologistDto>> getAllRadiologists() {
        return ResponseEntity.ok(radiologistService.getAllRadiologists());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RadiologistDto> updateRadiologist(@PathVariable Long id, @RequestBody RadiologistDto dto) {
        return ResponseEntity.ok(radiologistService.updateRadiologist(id, dto));
    }
}