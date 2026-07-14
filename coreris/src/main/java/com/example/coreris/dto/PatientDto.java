package com.example.coreris.dto;

import com.example.coreris.entity.type.BloodGroupType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientDto {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2,max = 50,message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Emails is required")
    @Email(message = "Please provide valid email address")
    private String email;

    @NotNull(message = "Mobile no. is required")
    @Digits(integer = 10,fraction = 0,message = "Mobile number must be exactly 10 digits")
    private Long mobileNo;

    @NotNull(message = "Blood group is required")
    private BloodGroupType bloodGroup;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;
}
