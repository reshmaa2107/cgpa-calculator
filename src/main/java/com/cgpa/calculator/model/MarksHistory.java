package com.cgpa.calculator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "marks_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MarksHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "semester_no", nullable = false)
    private Integer semesterNo;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "course_code")
    private String courseCode;

    @Column(name = "credit_hours", nullable = false)
    private Integer creditHours;

    @Column(name = "letter_grade", nullable = false)
    private String letterGrade;

    @Column(name = "grade_points", nullable = false, columnDefinition = "numeric(4,2)")
    private Double gradePoints;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() { recordedAt = LocalDateTime.now(); }
}