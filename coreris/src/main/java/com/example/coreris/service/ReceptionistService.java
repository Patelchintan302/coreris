package com.example.coreris.service;

import com.example.coreris.dto.ReceptionistDto;
import com.example.coreris.entity.Receptionists;
import com.example.coreris.repository.ReceptionistsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceptionistService {
    private final ModelMapper modelMapper;
    private final ReceptionistsRepository receptionistsRepository;

    public List<ReceptionistDto> getAllReceptionists(){
        return receptionistsRepository.findAll()
                .stream()
                .map(r -> modelMapper.map(r,ReceptionistDto.class))
                .collect(Collectors.toList());
    }

    public ReceptionistDto updateReceptionist(Long id,ReceptionistDto dto){
        Receptionists receptionist = receptionistsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receptionist with id "+id+" not found"));
        receptionist.setName(dto.getName());
        Receptionists saved  = receptionistsRepository.save(receptionist);
        return modelMapper.map(saved,ReceptionistDto.class);
    }
}
