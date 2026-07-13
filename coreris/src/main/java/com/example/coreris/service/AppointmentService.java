package com.example.coreris.service;

import com.example.coreris.entity.Appointment;
import com.example.coreris.exception_handler.AppointmentNotFoundException;
import com.example.coreris.repository.AppointmentRepository;
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
    private final ModelMapper modelmapper;

    public List<AppointmentDto> getAllAppointment(){
        return appointmentRepository.findAll()
                .stream()
                .map(app -> modelmapper.map(app,AppointmentDto.class))
                .collect(Collectors.toList());
    }

    public AppointmentDto getAppointmentById(Long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
        return modelmapper.map(appointment,AppointmentDto.class);
    }

    public AppointmentDto createAppointment(Appointment appointment){
        Appointment save = appointmentRepository.save(appointment);
        return modelmapper.map(save,AppointmentDto.class);
    }

    public void deleteAppointment(long id){
        appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
        appointmentRepository.deleteById(id);
    }

}
