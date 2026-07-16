package com.example.coreris.service;

import com.example.coreris.dto.AppointmentCreateDto;
import com.example.coreris.entity.Appointment;
import com.example.coreris.entity.Patient;
import com.example.coreris.exception_handler.AppointmentNotFoundException;
import com.example.coreris.exception_handler.PatientNotFoundException;
import com.example.coreris.repository.AppointmentRepository;
import com.example.coreris.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.example.coreris.dto.AppointmentDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    public List<AppointmentDto> getAllAppointment(){
        return appointmentRepository.findAll()
                .stream()
                .map(app -> modelMapper.map(app,AppointmentDto.class))
                .collect(Collectors.toList());
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
                .patient(patient)
                .build();
        Appointment save = appointmentRepository.save(appointment);
        return modelMapper.map(save,AppointmentDto.class);
    }

    public void deleteAppointment(long id){
        appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
        appointmentRepository.deleteById(id);
    }

}
