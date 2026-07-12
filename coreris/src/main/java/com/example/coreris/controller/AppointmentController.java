package com.example.coreris.controller;

import com.example.coreris.entity.Appointment;
import com.example.coreris.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/appointment")
@RestController
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointment(){
        return ResponseEntity.ok(appointmentService.getAllAppointment());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable long id){
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment){
        Appointment newAppointment = appointmentService.createAppoinment(appointment);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAppointment.getId())
                .toUri();

        return ResponseEntity.created(location).body(newAppointment);
    }
    @DeleteMapping("{id}")
    public void deleteAppointment(@PathVariable long id){
        appointmentService.deleteAppointment(id);
    }
}
