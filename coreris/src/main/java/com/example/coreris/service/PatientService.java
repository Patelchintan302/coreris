package com.example.coreris.service;

import com.example.coreris.dto.*;
import com.example.coreris.entity.Patient;
import com.example.coreris.exception_handler.PatientNotFoundException;
import com.example.coreris.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    @Transactional
    public Page<PatientDto> getAllPatients(Pageable pageable){
        return patientRepository.findAll(pageable)
                .map(patient -> modelMapper.map(patient, PatientDto.class));
    }

    @Transactional
    public PatientDto getPatientById(Long id){
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
        return modelMapper.map(patient,PatientDto.class);
    }
    @Transactional
    public PatientDto createPatient(PatientDto patientDto){
        Patient save = patientRepository.save(modelMapper.map(patientDto,Patient.class));
        return modelMapper.map(save,PatientDto.class);
    }

    @Transactional
    public PatientHistoryDto getPatientHistory(long patientId){
        Patient patient =  patientRepository.findById(patientId).orElseThrow(() -> new PatientNotFoundException(patientId));
        List<AppointmentHistoryDto> appointmentHistory = patient.getAppointments().stream()
                .map(appointment -> {
                            ScanResultDto scanResult = appointment.getScanResult() != null ?
                                    modelMapper.map(appointment.getScanResult(), ScanResultDto.class) : null;

                            if(scanResult != null){
                                scanResult.setAppointmentId(appointment.getId());
                            }

                            ReportDto report = appointment.getReport() != null ?
                                    modelMapper.map(appointment.getReport(), ReportDto.class) : null;

                            if(report != null){
                                report.setAppointmentId(appointment.getId());
                            }

                            return AppointmentHistoryDto.builder()
                                    .id(appointment.getId())
                                    .createdAt(appointment.getCreatedAt())
                                    .appointmentTime(appointment.getAppointmentTime())
                                    .scanResult(scanResult)
                                    .report(report)
                                    .build();
                        }
                ).collect(Collectors.toList());

        return PatientHistoryDto.builder()
                .id(patient.getId())
                .name(patient.getName())
                .mobileNo(patient.getMobileNo())
                .dob(patient.getDob())
                .email(patient.getEmail())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .appointments(appointmentHistory)
                .build();
    }


}
