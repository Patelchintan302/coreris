package com.example.coreris.service;

import com.example.coreris.dto.RadiologistDto;
import com.example.coreris.dto.UserDto;
import com.example.coreris.entity.Radiologists;
import com.example.coreris.entity.Receptionists;
import com.example.coreris.entity.Technicians;
import com.example.coreris.entity.User;
import com.example.coreris.entity.type.RoleType;
import com.example.coreris.exception_handler.UserNotFoundException;
import com.example.coreris.repository.RadiologistsRepository;
import com.example.coreris.repository.ReceptionistsRepository;
import com.example.coreris.repository.TechniciansRepository;
import com.example.coreris.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ReceptionistsRepository receptionistsRepository;
    private final TechniciansRepository techniciansRepository;
    private final RadiologistsRepository radiologistsRepository;
    private final ModelMapper modelMapper;

    @Transactional
    private UserDto createUser(UserDto userDto){
        User user = modelMapper.map(userDto,User.class);
        User savedUser = userRepository.save(user);
        if(userDto.getRole() == RoleType.RECEPTIONIST){
            Receptionists receptionist = modelMapper.map(userDto, Receptionists.class);
            receptionist.setUser(savedUser); // Establish @MapsId relationship
            receptionistsRepository.save(receptionist);
        } else if(userDto.getRole() == RoleType.TECHNICIAN){
            Technicians technician = modelMapper.map(userDto,Technicians.class);
            technician.setUser(savedUser);
            techniciansRepository.save(technician);
        } else if(userDto.getRole() == RoleType.RADIOLOGIST){
            Radiologists radiologist = modelMapper.map(userDto,Radiologists.class);
            radiologist.setUser(savedUser);
            radiologistsRepository.save(radiologist);
        }
        UserDto response = modelMapper.map(savedUser, UserDto.class);
        response.setName(userDto.getName()); // Set the name back
        return response;
    }

    @Transactional
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        String name = "";
        if (user.getRole() == RoleType.RECEPTIONIST) {
            Receptionists receptionist = receptionistsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Receptionist profile not found"));
            // ModelMapper maps name automatically
            name = receptionist.getName();
        } else if (user.getRole() == RoleType.TECHNICIAN) {
            Technicians technician = techniciansRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Technician profile not found"));
            name = technician.getName();
        } else if (user.getRole() == RoleType.RADIOLOGIST) {
            Radiologists radiologist = radiologistsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Radiologist profile not found"));
            name = radiologist.getName();
        }

        // Map User entity to UserDto, then set the retrieved name
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setName(name);
        return userDto;
    }
}
