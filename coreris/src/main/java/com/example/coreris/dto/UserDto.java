package com.example.coreris.dto;

import com.example.coreris.entity.type.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
public class UserDto {
    private Long id;

    @NotNull(message = "Role is Required")
    private RoleType role;

    @NotBlank(message = "Name is Required")
    private String name;
}
