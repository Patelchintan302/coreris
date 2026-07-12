package com.example.coreris.service;

import com.example.coreris.entity.Appointment;
import com.example.coreris.exception_handler.AppoinmentNotFoundException;
import com.example.coreris.exception_handler.PatientNotFoundException;
import com.example.coreris.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public List<Appointment> getAllAppointment(){
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id){
        return appointmentRepository.findById(id).orElseThrow(() -> new AppoinmentNotFoundException(id));
    }

    public Appointment createAppoinment(Appointment appointment){
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(long id){
        appointmentRepository.findById(id).orElseThrow(() -> new AppoinmentNotFoundException(id));
        appointmentRepository.deleteById(id);
    }

}
