package com.example.coreris.service;

import com.example.coreris.dto.PatientDto;
import com.example.coreris.entity.Patient;
import com.example.coreris.exception_handler.PatientNotFoundException;
import com.example.coreris.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    @Transactional
    public List<PatientDto> getAllPatients(){
        return patientRepository.findAll()
                .stream()
                .map(patient -> modelMapper.map(patient, PatientDto.class))
                .collect(Collectors.toList());
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
    public void deletePatient(long id){
        patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
        patientRepository.deleteById(id);
    }


}
