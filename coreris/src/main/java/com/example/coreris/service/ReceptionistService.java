package com.example.coreris.service;

import com.example.coreris.dto.ReceptionistDto;
import com.example.coreris.entity.Receptionist;
import com.example.coreris.repository.ReceptionistRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceptionistService {
    private final ModelMapper modelMapper;
    private final ReceptionistRepository receptionistRepository;

    public List<ReceptionistDto> getAllReceptionists(){
        return receptionistRepository.findAll()
                .stream()
                .map(r -> modelMapper.map(r,ReceptionistDto.class))
                .collect(Collectors.toList());
    }

    public ReceptionistDto updateReceptionist(Long id,ReceptionistDto dto){
        Receptionist receptionist = receptionistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receptionist with id "+id+" not found"));
        receptionist.setName(dto.getName());
        Receptionist saved  = receptionistRepository.save(receptionist);
        return modelMapper.map(saved,ReceptionistDto.class);
    }
}
