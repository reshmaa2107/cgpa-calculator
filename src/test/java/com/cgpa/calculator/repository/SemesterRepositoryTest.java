package com.cgpa.calculator.repository;

import com.cgpa.calculator.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SemesterRepositoryTest {

    @Autowired SemesterRepository semesterRepository;
    @Autowired StudentRepository  studentRepository;

    private Student student;

    @BeforeEach
    void setUp() {
        student = studentRepository.save(Student.builder()
                .name("Test Student")
                .email("test@example.com")
                .rollNumber("TEST001")
                .build());
    }

    @Test
    @DisplayName("findByStudentId: returns semesters in ascending order")
    void findByStudentId_orderedBySemesterNumber() {
        semesterRepository.save(Semester.builder()
                .student(student).semesterNumber(2).academicYear("2024-25").build());
        semesterRepository.save(Semester.builder()
                .student(student).semesterNumber(1).academicYear("2023-24").build());

        List<Semester> result = semesterRepository
                .findByStudentIdOrderBySemesterNumberAsc(student.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSemesterNumber()).isEqualTo(1);
        assertThat(result.get(1).getSemesterNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("calculateCgpaByStudentId: averages semester GPAs correctly")
    void calculateCgpa_averagesCorrectly() {
        semesterRepository.save(Semester.builder()
                .student(student).semesterNumber(1).gpa(9.0).build());
        semesterRepository.save(Semester.builder()
                .student(student).semesterNumber(2).gpa(7.0).build());

        Optional<Double> cgpa = semesterRepository
                .calculateCgpaByStudentId(student.getId());

        assertThat(cgpa).isPresent();
        assertThat(cgpa.get()).isEqualTo(8.0);
    }

    @Test
    @DisplayName("calculateCgpaByStudentId: returns empty when no graded semesters")
    void calculateCgpa_noGrades_returnsEmpty() {
        semesterRepository.save(Semester.builder()
                .student(student).semesterNumber(1).gpa(null).build());

        Optional<Double> cgpa = semesterRepository
                .calculateCgpaByStudentId(student.getId());

        assertThat(cgpa).isEmpty();
    }
}