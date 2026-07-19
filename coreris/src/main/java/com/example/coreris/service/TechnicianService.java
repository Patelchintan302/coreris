package com.example.coreris.service;

import com.example.coreris.dto.TechnicianDto;
import com.example.coreris.entity.Technicians;
import com.example.coreris.repository.TechniciansRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicianService {

    private final TechniciansRepository technicianRepository;
    private final ModelMapper modelMapper;

    public List<TechnicianDto> getAllTechnicians() {
        return technicianRepository.findAll()
                .stream()
                .map(t -> modelMapper.map(t, TechnicianDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public TechnicianDto updateTechnician(Long id, TechnicianDto dto) {
        Technicians technician = technicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Technician not found with id: " + id));
        technician.setName(dto.getName());
        Technicians saved = technicianRepository.save(technician);
        return modelMapper.map(saved, TechnicianDto.class);
    }
}