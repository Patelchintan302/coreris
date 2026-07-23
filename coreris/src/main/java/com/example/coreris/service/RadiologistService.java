package com.example.coreris.service;

import com.example.coreris.dto.RadiologistDto;
import com.example.coreris.entity.Radiologist;
import com.example.coreris.repository.RadiologistRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RadiologistService {

    private final RadiologistRepository radiologistRepository;
    private final ModelMapper modelMapper;

    public List<RadiologistDto> getAllRadiologists() {
        return radiologistRepository.findAll()
                .stream()
                .map(r -> modelMapper.map(r, RadiologistDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public RadiologistDto updateRadiologist(Long id, RadiologistDto dto) {
        Radiologist radiologist = radiologistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Radiologist not found with id: " + id));
        radiologist.setName(dto.getName());
        Radiologist saved = radiologistRepository.save(radiologist);
        return modelMapper.map(saved, RadiologistDto.class);
    }
}