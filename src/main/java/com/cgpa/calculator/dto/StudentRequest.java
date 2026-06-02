package com.cgpa.calculator.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StudentRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    private String college;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}