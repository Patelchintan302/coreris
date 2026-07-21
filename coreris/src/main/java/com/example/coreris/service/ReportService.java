package com.example.coreris.service;

import com.example.coreris.dto.ReportCreateDto;
import com.example.coreris.dto.ReportDto;
import com.example.coreris.entity.Appointment;
import com.example.coreris.entity.Radiologists;
import com.example.coreris.entity.Report;
import com.example.coreris.exception_handler.AppointmentNotFoundException;
import com.example.coreris.exception_handler.UserNotFoundException;
import com.example.coreris.repository.AppointmentRepository;
import com.example.coreris.repository.RadiologistsRepository;
import com.example.coreris.repository.ReportRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public ReportDto createReport(Long appointmentId, ReportCreateDto reportCreateDto) {
        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        Radiologists radiologist = radiologistsRepository
                .findById(reportCreateDto.getRadiologistId())
                .orElseThrow(() -> new UserNotFoundException(reportCreateDto.getRadiologistId()));

        Report report = Report.builder()
                .finding(reportCreateDto.getFinding())
                .appointment(appointment)
                .radiologist(radiologist)
                .build();
        Report savedReport = reportRepository.save(report);
        ReportDto reportDto = modelMapper.map(savedReport, ReportDto.class);
        reportDto.setAppointmentId(appointmentId);
        return reportDto;
    }



}
