package com.example.coreris.controller;

import com.example.coreris.dto.AppointmentCreateDto;
import com.example.coreris.dto.AppointmentDto;
import com.example.coreris.entity.type.StatusType;
import com.example.coreris.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/appointments")
@RestController
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<Page<AppointmentDto>> getAllAppointment(
            @RequestParam(required = false) StatusType status,
            @PageableDefault(page = 0,size = 10,sort = "appointmentTime", direction = Sort.Direction.ASC) Pageable pageable
    ){
        if(status != null) {
            return ResponseEntity.ok(appointmentService.getAllAppointmentByStatus(status,pageable));
        }
        return ResponseEntity.ok(appointmentService.getAllAppointment(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable long id){
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody AppointmentCreateDto appointmentDto){
        AppointmentDto newAppointment = appointmentService.createAppointment(appointmentDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAppointment.getId())
                .toUri();

        return ResponseEntity.created(location).body(newAppointment);
    }
    @PatchMapping("{id}/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(@PathVariable long id){
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentDto> updateStatus(
            @PathVariable long id,
            @RequestParam StatusType status
    ) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }
}
