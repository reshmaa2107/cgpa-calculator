package com.cgpa.calculator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "semester")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "semester_number", nullable = false)
    private Integer semesterNumber;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(columnDefinition = "numeric(4,2)")
    private Double gpa;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();
}