package com.example.coreris.controller;

import com.example.coreris.dto.PatientDto;
import com.example.coreris.dto.PatientHistoryDto;
import com.example.coreris.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    //sp note :- accessible to any authenticated user
    @GetMapping
    public ResponseEntity<Page<PatientDto>> getAllPatients(
            @PageableDefault(page = 0,size = 10,sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ){
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    //sp note :- accessible to any authenticated user
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable long id){ //@Param("id")
        PatientDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    //sp note :- accessible to any authenticated user
    @GetMapping("/{id}/history")
    public ResponseEntity<PatientHistoryDto> getPatientHistory(@PathVariable long id){
        PatientHistoryDto patientHistoryDto = patientService.getPatientHistory(id);
        return ResponseEntity.ok(patientHistoryDto);
    }

    //sp note :- accessible to only receptionist and admin
    @PostMapping
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto patient){
        PatientDto newPatient = patientService.createPatient(patient);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newPatient.getId())
                .toUri();
        return ResponseEntity.created(location).body(newPatient);
    }
}
