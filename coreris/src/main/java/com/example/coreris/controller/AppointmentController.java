package com.example.coreris.controller;

import com.example.coreris.dto.AppointmentDto;
import com.example.coreris.entity.Appointment;
import com.example.coreris.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/appointments")
@RestController
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointment(){
        return ResponseEntity.ok(appointmentService.getAllAppointment());
    }
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable long id){
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody Appointment appointment){
        AppointmentDto newAppointment = appointmentService.createAppointment(appointment);
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
