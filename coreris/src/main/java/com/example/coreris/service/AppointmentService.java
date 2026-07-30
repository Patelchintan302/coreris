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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.coreris.dto.AppointmentDto;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    public Page<AppointmentDto> getAllAppointment(Pageable pageable){
        return appointmentRepository.findAll(pageable)
                .map(app -> modelMapper.map(app,AppointmentDto.class));
    }

    public Page<AppointmentDto> getAllAppointmentByStatus(StatusType status, Pageable pageable){
        return appointmentRepository.findByStatus(status,pageable)
                .map(app -> modelMapper.map(app,AppointmentDto.class));
    }

    @Transactional
    public AppointmentDto updateAppointmentStatus(long id, StatusType newStatus){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
        appointment.setStatus(newStatus);
        return modelMapper.map(appointmentRepository.save(appointment),AppointmentDto.class);
    }

    public Page<AppointmentDto> getAllBookedAppointment(Pageable pageable){
        return appointmentRepository.findByStatus(StatusType.BOOKED,pageable)
                .map(map -> modelMapper.map(map,AppointmentDto.class));
    }

    public AppointmentDto getAppointmentById(Long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
        return modelMapper.map(appointment,AppointmentDto.class);
    }
    @Transactional
    public AppointmentDto createAppointment(AppointmentCreateDto appointmentDto){
        Patient patient = patientRepository.findById(appointmentDto.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException(appointmentDto.getPatientId()));
        Appointment appointment = Appointment.builder()
                .appointmentTime(appointmentDto.getAppointmentTime())
                .status(appointmentDto.getStatus())
                .patient(patient)
                .build();
        Appointment save = appointmentRepository.save(appointment);
        return modelMapper.map(save,AppointmentDto.class);
    }

    @Transactional
    public AppointmentDto cancelAppointment(long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
        appointment.setStatus(StatusType.CANCELLED);
        appointmentRepository.save(appointment);
        return modelMapper.map(appointment,AppointmentDto.class);
    }

}
