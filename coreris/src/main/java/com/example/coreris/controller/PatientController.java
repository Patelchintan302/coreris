package com.example.coreris.controller;

import com.example.coreris.dto.PatientDto;
import com.example.coreris.entity.Patient;
import com.example.coreris.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllPatients(){
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@RequestBody Patient patient){
        PatientDto newPatient = patientService.createPatient(patient);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newPatient.getId())
                .toUri();
        return ResponseEntity.created(location).body(newPatient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable long id){ //@Param("id")
        PatientDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable long id){
        patientService.deletePatient(id);
    }
}
