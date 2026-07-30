package com.example.coreris.service;

import com.example.coreris.dto.ReportCreateDto;
import com.example.coreris.dto.ReportDto;
import com.example.coreris.entity.Appointment;
import com.example.coreris.entity.Radiologist;
import com.example.coreris.entity.Report;
import com.example.coreris.entity.type.StatusType;
import com.example.coreris.exception_handler.AppointmentNotFoundException;
import com.example.coreris.exception_handler.ReportNotFoundException;
import com.example.coreris.exception_handler.UserNotFoundException;
import com.example.coreris.repository.AppointmentRepository;
import com.example.coreris.repository.RadiologistRepository;
import com.example.coreris.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final AppointmentRepository appointmentRepository;
    private final RadiologistRepository radiologistRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ReportDto createReport(Long appointmentId, Long radiologistId,ReportCreateDto reportCreateDto) {
        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        Radiologist radiologist = radiologistRepository
                .findById(radiologistId)
                .orElseThrow(() -> new UserNotFoundException(radiologistId));

        Report report = Report.builder()
                .finding(reportCreateDto.getFinding())
                .appointment(appointment)
                .radiologist(radiologist)
                .build();
        //sp note:- status is changed now completed
        appointment.setStatus(StatusType.COMPLETED);
        appointmentRepository.save(appointment);
        Report savedReport = reportRepository.save(report);
        ReportDto reportDto = modelMapper.map(savedReport, ReportDto.class);
        reportDto.setAppointmentId(appointmentId);
        return reportDto;
    }

    public ReportDto getReportById(Long id) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new ReportNotFoundException(id));
        ReportDto reportDto = modelMapper.map(report, ReportDto.class);
        reportDto.setAppointmentId(report.getAppointment().getId());
        return reportDto;
    }

    public ReportDto getReportByAppointmentId(Long appointmentId){
        appointmentRepository.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        Report report = reportRepository.findByAppointmentId(appointmentId).orElseThrow(() -> new ReportNotFoundException("Report not found with AppointmentId : " + appointmentId));
        ReportDto reportDto = modelMapper.map(report, ReportDto.class);
        reportDto.setAppointmentId(appointmentId);
        return reportDto;
    }


}
