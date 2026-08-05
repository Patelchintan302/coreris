package com.example.coreris.service;

import com.example.coreris.dto.UserDto;
import com.example.coreris.entity.Radiologist;
import com.example.coreris.entity.Receptionist;
import com.example.coreris.entity.Technician;
import com.example.coreris.entity.User;
import com.example.coreris.entity.type.RoleType;
import com.example.coreris.exception_handler.UserNotFoundException;
import com.example.coreris.repository.RadiologistRepository;
import com.example.coreris.repository.ReceptionistRepository;
import com.example.coreris.repository.TechnicianRepository;
import com.example.coreris.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ReceptionistRepository receptionistRepository;
    private final TechnicianRepository technicianRepository;
    private final RadiologistRepository radiologistRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public UserDto createUser(UserDto userDto){
        User user = modelMapper.map(userDto,User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User savedUser = userRepository.save(user);
        if(userDto.getRole() == RoleType.RECEPTIONIST){
            Receptionist receptionist = modelMapper.map(userDto, Receptionist.class);
            receptionist.setUser(savedUser); // Establish @MapsId relationship
            receptionistRepository.save(receptionist);
        } else if(userDto.getRole() == RoleType.TECHNICIAN){
            Technician technician = modelMapper.map(userDto, Technician.class);
            technician.setUser(savedUser);
            technicianRepository.save(technician);
        } else if(userDto.getRole() == RoleType.RADIOLOGIST){
            Radiologist radiologist = modelMapper.map(userDto, Radiologist.class);
            radiologist.setUser(savedUser);
            radiologistRepository.save(radiologist);
        }
        UserDto response = modelMapper.map(savedUser, UserDto.class);
        response.setName(userDto.getName()); // Set the name back

        log.info("Administrator created new staff user account: '{}' with role: {}", userDto.getUsername(), userDto.getRole());

        return response;
    }

    @Transactional
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        String name = "";
        if (user.getRole() == RoleType.RECEPTIONIST) {
            Receptionist receptionist = receptionistRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Receptionist profile not found"));
            // ModelMapper maps name automatically
            name = receptionist.getName();
        } else if (user.getRole() == RoleType.TECHNICIAN) {
            Technician technician = technicianRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Technician profile not found"));
            name = technician.getName();
        } else if (user.getRole() == RoleType.RADIOLOGIST) {
            Radiologist radiologist = radiologistRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Radiologist profile not found"));
            name = radiologist.getName();
        }

        // Map User entity to UserDto, then set the retrieved name
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setName(name);

        log.debug("Fetching user profile details for ID: {}", id);

        return userDto;
    }
}
