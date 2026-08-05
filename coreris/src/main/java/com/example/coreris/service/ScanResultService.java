package com.example.coreris.service;

import com.example.coreris.dto.ScanResultCreateDto;
import com.example.coreris.dto.ScanResultDto;
import com.example.coreris.entity.*;
import com.example.coreris.entity.type.StatusType;
import com.example.coreris.exception_handler.AppointmentNotFoundException;
import com.example.coreris.exception_handler.ScanResultNotFoundException;
import com.example.coreris.exception_handler.UserNotFoundException;
import com.example.coreris.repository.AppointmentRepository;
import com.example.coreris.repository.ScanResultRepository;
import com.example.coreris.repository.TechnicianRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.coreris.entity.type.RoleType;
import com.example.coreris.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScanResultService {
    private final ScanResultRepository scanResultRepository;
    private final TechnicianRepository technicianRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    @Transactional
    public ScanResultDto createScanResult(Long appointmentId, Long technicianId, MultipartFile file, ScanResultCreateDto scanResultCreateDto) {

        log.info("Technician ID {} uploaded a new scan result for Appointment ID: {}", technicianId, appointmentId);

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        Technician technician = technicianRepository
                .findById(technicianId)
                .orElseThrow(() -> new UserNotFoundException(technicianId));

        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUrl = "/scans/download/" + fileName;


        ScanResult scanResult = ScanResult.builder()
                .scanDetails(scanResultCreateDto.getScanDetails())
                .appointment(appointment)
                .imageUrl(fileDownloadUrl)
                .technician(technician)
                .build();
        //sp note:- status is changed now scan complete
        appointment.setStatus(StatusType.SCAN_COMPLETE);
        appointmentRepository.save(appointment);
        ScanResult savedScanResult = scanResultRepository.save(scanResult);
        ScanResultDto scanResultDto = modelMapper.map(savedScanResult, ScanResultDto.class);
        scanResultDto.setAppointmentId(appointmentId);

        log.info("uploaded a new scan result for Appointment ID: {} is saved in database", appointmentId);

        return scanResultDto;
    }

    public ScanResultDto getScanResultById(Long id) {

        log.debug("trying to Fetch details of scan result ID: {}", id);

        ScanResult scanResult = scanResultRepository.findById(id).orElseThrow(() -> new ScanResultNotFoundException(id));
        ScanResultDto scanResultDto = modelMapper.map(scanResult, ScanResultDto.class);
        scanResultDto.setAppointmentId(scanResult.getAppointment().getId());

        log.debug("Fetching details of scan result ID: {}", id);

        return scanResultDto;
    }

    public ScanResultDto getScanResultByAppointmentId(Long appointmentId){

        log.debug("trying to Fetch scan result details for Appointment ID: {}", appointmentId);

        appointmentRepository.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        ScanResult scanResult = scanResultRepository.findByAppointmentId(appointmentId).orElseThrow(() -> new ScanResultNotFoundException("Scan Result not found with AppointmentId : " + appointmentId));
        ScanResultDto scanResultDto = modelMapper.map(scanResult, ScanResultDto.class);
        scanResultDto.setAppointmentId(appointmentId);

        log.debug("Fetching scan result details for Appointment ID: {}", appointmentId);

        return scanResultDto;
    }

    @Transactional
    public ScanResultDto updateScanResult(Long appointmentId, MultipartFile file, ScanResultCreateDto scanResultCreateDto) {

        log.info("Technician trying to update scan result (new file: {}) for Appointment ID: {}", (file != null && !file.isEmpty()), appointmentId);

        ScanResult scanResult = scanResultRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ScanResultNotFoundException("Scan Result not found for appointment: " + appointmentId));
        if(file != null && !file.isEmpty()){
            String oldFileUrl = scanResult.getImageUrl();
            if(oldFileUrl != null && oldFileUrl.contains("/scans/download/")){
                String oldFileName = oldFileUrl.substring(oldFileUrl.lastIndexOf("/")+1);
                fileStorageService.deleteFile(oldFileName);
            }
            String newFileName = fileStorageService.storeFile(file);
            scanResult.setImageUrl(newFileName);
        }
        scanResult.setScanDetails(scanResultCreateDto.getScanDetails());

        ScanResult savedScanResult = scanResultRepository.save(scanResult);

        ScanResultDto scanResultDto = modelMapper.map(savedScanResult, ScanResultDto.class);
        scanResultDto.setAppointmentId(appointmentId);
        log.info("Technician updated scan result (new file: {}) for Appointment ID: {}", (file != null && !file.isEmpty()), appointmentId);
        return scanResultDto;
    }

    // 👈 ADD THIS OVERLOADED METHOD FOR THE DATABASE SEEDER (3 parameters)
    @Transactional
    public ScanResultDto createScanResult(Long appointmentId, Long technicianId, ScanResultCreateDto scanResultCreateDto) {
        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        Technician technician = technicianRepository
                .findById(technicianId)
                .orElseThrow(() -> new UserNotFoundException(technicianId));

        ScanResult scanResult = ScanResult.builder()
                .scanDetails(scanResultCreateDto.getScanDetails())
                .appointment(appointment)
                .imageUrl(scanResultCreateDto.getImageUrl()) // 👈 Uses the mock URL from the DTO directly
                .technician(technician)
                .build();

        appointment.setStatus(StatusType.SCAN_COMPLETE);
        appointmentRepository.save(appointment);

        ScanResult savedScanResult = scanResultRepository.save(scanResult);
        ScanResultDto scanResultDto = modelMapper.map(savedScanResult, ScanResultDto.class);
        scanResultDto.setAppointmentId(appointmentId);

        log.info("Database Seeder created mock scan result for Appointment ID: {}", appointmentId);

        return scanResultDto;
    }

}
