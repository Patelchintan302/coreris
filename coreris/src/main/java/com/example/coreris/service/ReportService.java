package com.example.coreris.service;

import com.example.coreris.repository.AppointmentRepository;
import com.example.coreris.repository.RadiologistsRepository;
import com.example.coreris.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ModelMapper modelMapper;
    private final AppointmentRepository appointmentRepository;
    private final RadiologistsRepository radiologistsRepository;


}
