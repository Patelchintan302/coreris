package com.example.coreris.service;

import com.example.coreris.dto.AppointmentCreateDto;
import com.example.coreris.entity.Appointment;
import com.example.coreris.entity.Patient;
import com.example.coreris.entity.type.StatusType;
import com.example.coreris.exception_handler.AppointmentNotFoundException;
import com.example.coreris.exception_handler.PatientNotFoundException;
import com.example.coreris.repository.AppointmentRepository;
import com.example.coreris.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.coreris.dto.AppointmentDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    public Page<AppointmentDto> getAllAppointment(Pageable pageable){

        log.debug("Fetching all appointments list. Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return appointmentRepository.findAll(pageable)
                .map(app -> modelMapper.map(app,AppointmentDto.class));
    }

    public Page<AppointmentDto> getAllAppointmentByStatus(StatusType status, Pageable pageable){

        log.debug("Filtering appointments queue for status: {}. Page: {}, Size: {}", status, pageable.getPageNumber(), pageable.getPageSize());

        return appointmentRepository.findByStatus(status,pageable)
                .map(app -> modelMapper.map(app,AppointmentDto.class));
    }

    @Transactional
    public AppointmentDto updateAppointmentStatus(long id, StatusType newStatus){

        log.info("trying to update Appointment ID: {} status to: {}", id, newStatus);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
        appointment.setStatus(newStatus);

        log.info("Transitioned Appointment ID: {} status to: {}", id, newStatus);

        return modelMapper.map(appointmentRepository.save(appointment),AppointmentDto.class);
    }

    public Page<AppointmentDto> getAllBookedAppointment(Pageable pageable){

        log.debug("Fetching lobby queue of BOOKED appointments. Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return appointmentRepository.findByStatus(StatusType.BOOKED,pageable)
                .map(map -> modelMapper.map(map,AppointmentDto.class));
    }

    public AppointmentDto getAppointmentById(Long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));

        log.debug("Fetching details for Appointment ID: {}", id);

        return modelMapper.map(appointment,AppointmentDto.class);
    }
    @Transactional
    public AppointmentDto createAppointment(AppointmentCreateDto appointmentDto){

        log.info("trying to Book new appointment for Patient ID: {}", appointmentDto.getPatientId());

        Patient patient = patientRepository.findById(appointmentDto.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException(appointmentDto.getPatientId()));
        Appointment appointment = Appointment.builder()
                .appointmentTime(appointmentDto.getAppointmentTime())
                .status(appointmentDto.getStatus())
                .scanType(appointmentDto.getScanType())
                .patient(patient)
                .build();
        Appointment save = appointmentRepository.save(appointment);

        log.info("Booked new {} appointment for Patient ID: {}", appointmentDto.getScanType(), appointmentDto.getPatientId());

        return modelMapper.map(save,AppointmentDto.class);
    }

    @Transactional
    public AppointmentDto cancelAppointment(long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
        appointment.setStatus(StatusType.CANCELLED);
        appointmentRepository.save(appointment);

        log.info("Soft-deleted (cancelled) Appointment ID: {}", id);

        return modelMapper.map(appointment,AppointmentDto.class);
    }

}
