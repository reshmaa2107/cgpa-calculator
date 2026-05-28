package com.cgpa.calculator.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GradeRequest {
    @NotNull
    private UUID courseId;

    @NotBlank(message = "Letter grade is required")
    private String letterGrade;   // "A", "B+", "C", etc.

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double gradePoints;
}