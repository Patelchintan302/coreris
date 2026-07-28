package com.example.coreris.dto;

import com.example.coreris.entity.type.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotNull(message = "Role is Required")
    private RoleType role;

    @NotBlank(message = "Name is Required")
    private String name;

    @NotBlank(message = "Username id Required")
    private String username;

    @NotBlank(message = "password is Required")
    private String password;
}
