package com.cgpa.calculator.dto;

import lombok.*;
import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CgpaResponse {
    private UUID studentId;
    private String studentName;
    private String rollNumber;
    private Double cgpa;
    private List<SemesterSummary> semesters;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SemesterSummary {
        private Integer semesterNumber;
        private String academicYear;
        private Double gpa;
        private Integer totalCredits;
        private Integer coursesCount;
    }
}