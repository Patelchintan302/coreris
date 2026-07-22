package com.example.coreris.service;

import com.example.coreris.dto.ScanResultCreateDto;
import com.example.coreris.dto.ScanResultDto;
import com.example.coreris.entity.*;
import com.example.coreris.exception_handler.AppointmentNotFoundException;
import com.example.coreris.exception_handler.ScanResultNotFoundException;
import com.example.coreris.exception_handler.UserNotFoundException;
import com.example.coreris.repository.AppointmentRepository;
import com.example.coreris.repository.ScanResultRepository;
import com.example.coreris.repository.TechniciansRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScanResultService {
    private final ScanResultRepository scanResultRepository;
    private final TechniciansRepository techniciansRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ScanResultDto createScanResult(Long appointmentId, Long technicianId, ScanResultCreateDto scanResultCreateDto) {
        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        Technicians technician = techniciansRepository
                .findById(technicianId)
                .orElseThrow(() -> new UserNotFoundException(technicianId));

        ScanResult scanResult = ScanResult.builder()
                .scanDetails(scanResultCreateDto.getScanDetails())
                .imageUrl(scanResultCreateDto.getImageUrl())
                .appointment(appointment)
                .technician(technician)
                .build();
        ScanResult savedScanResult = scanResultRepository.save(scanResult);
        ScanResultDto scanResultDto = modelMapper.map(savedScanResult, ScanResultDto.class);
        scanResultDto.setAppointmentId(appointmentId);
        return scanResultDto;
    }

    public ScanResultDto getScanResultById(Long id) {
        ScanResult scanResult = scanResultRepository.findById(id).orElseThrow(() -> new ScanResultNotFoundException(id));
        ScanResultDto scanResultDto = modelMapper.map(scanResult, ScanResultDto.class);
        scanResultDto.setAppointmentId(scanResult.getAppointment().getId());
        return scanResultDto;
    }

    public ScanResultDto getScanResultByAppointmentId(Long appointmentId){
        appointmentRepository.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        ScanResult scanResult = scanResultRepository.findByAppointmentId(appointmentId).orElseThrow(() -> new ScanResultNotFoundException("Scan Result not found with AppointmentId : " + appointmentId));
        ScanResultDto scanResultDto = modelMapper.map(scanResult, ScanResultDto.class);
        scanResultDto.setAppointmentId(appointmentId);
        return scanResultDto;
    }


}
