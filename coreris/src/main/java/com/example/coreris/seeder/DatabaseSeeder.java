package com.example.coreris.seeder;

import com.example.coreris.dto.*;
import com.example.coreris.entity.type.BloodGroupType;
import com.example.coreris.entity.type.RoleType;
import com.example.coreris.repository.UserRepository;
import com.example.coreris.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final ScanResultService scanResultService;
    private final ReportService reportService;


    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count()==0){
            System.out.println(">>> Database is empty. Seeding realistic sample data...");

            // 1. Seed Staff Users (will automatically create corresponding profiles)
            userService.createUser(UserDto.builder().name("Jane Tech").role(RoleType.TECHNICIAN).build()); // ID 1
            userService.createUser(UserDto.builder().name("Dr. John Smith").role(RoleType.RADIOLOGIST).build()); // ID 2
            userService.createUser(UserDto.builder().name("Alice Reception").role(RoleType.RECEPTIONIST).build()); // ID 3

            // 2. Seed Patients
            PatientDto p1 = patientService.createPatient(PatientDto.builder()
                    .name("Robert Brown")
                    .email("robert.brown@example.com")
                    .mobileNo(9876543210L)
                    .bloodGroup(BloodGroupType.O_POSITIVE)
                    .dob(LocalDate.of(1990, 5, 15))
                    .gender("Male")
                    .build()); // ID 1

            PatientDto p2 = patientService.createPatient(PatientDto.builder()
                    .name("Sarah Connor")
                    .email("sarah.connor@example.com")
                    .mobileNo(8765432109L)
                    .bloodGroup(BloodGroupType.AB_NEGATIVE)
                    .dob(LocalDate.of(1985, 11, 10))
                    .gender("Female")
                    .build()); // ID 2

            // 3. Seed Appointments
            AppointmentDto a1 = appointmentService.createAppointment(AppointmentCreateDto.builder()
                    .appointmentTime(LocalDateTime.now().plusDays(2))
                    .patientId(p1.getId())
                    .build()); // ID 1

            AppointmentDto a2 = appointmentService.createAppointment(AppointmentCreateDto.builder()
                    .appointmentTime(LocalDateTime.now().plusDays(5))
                    .patientId(p2.getId())
                    .build()); // ID 2

            scanResultService.createScanResult(
                    a1.getId(),
                    1L, // Technician ID
                    ScanResultCreateDto.builder()
                            .scanDetails("Chest X-Ray shows normal lung patterns and heart size.")
                            .imageUrl("https://example.com/images/chest-xray-001.jpg")
                            .build()
            );

            // 5. Seed Diagnostic Report for Patient 1
            reportService.createReport(
                    a1.getId(),
                    2L, // Radiologist ID
                    ReportCreateDto.builder()
                            .finding("Radiograph displays clean fields. No signs of bronchitis or pneumonia.")
                            .build()
            );

            // 6. Seed Scan Result only (no report yet) for Patient 2 to simulate an active workflow
            scanResultService.createScanResult(
                    a2.getId(),
                    1L, // Technician ID
                    ScanResultCreateDto.builder()
                            .scanDetails("Abdominal ultrasound shows minor gallbladder inflammation.")
                            .imageUrl("https://example.com/images/abdomen-ultrasound-002.jpg")
                            .build()
            );

            System.out.println(">>> Database seeding complete!");
        } else {
            System.out.println(">>> Database already contains users. Skipping seeder.");
        }
    }
}
