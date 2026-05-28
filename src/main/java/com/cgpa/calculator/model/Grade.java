package com.cgpa.calculator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "grade")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "letter_grade", nullable = false)
    private String letterGrade;

    @Column(name = "grade_points", nullable = false, columnDefinition = "numeric(4,2)")
    private Double gradePoints;   // 10.0, 9.0, 8.0, etc.
}