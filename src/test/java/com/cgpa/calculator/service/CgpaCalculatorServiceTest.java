package com.cgpa.calculator.service;

import com.cgpa.calculator.dto.GradeRequest;
import com.cgpa.calculator.dto.StudentRequest;
import com.cgpa.calculator.model.*;
import com.cgpa.calculator.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CgpaCalculatorServiceTest {

    @Mock StudentRepository studentRepository;
    @Mock SemesterRepository semesterRepository;
    @Mock CourseRepository courseRepository;
    @Mock GradeRepository gradeRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock MarksHistoryRepository marksHistoryRepository;

    @InjectMocks CgpaCalculatorService service;

    @Test
    @DisplayName("createStudent: saves and returns student")
    void createStudent_success() {
        StudentRequest req = new StudentRequest("Arjun", "arjun@test.com", "CS001", "Anna University", "password123");
        when(studentRepository.existsByEmail("arjun@test.com")).thenReturn(false);
        when(studentRepository.existsByRollNumber("CS001")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(studentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Student result = service.createStudent(req);

        assertThat(result.getName()).isEqualTo("Arjun");
        assertThat(result.getEmail()).isEqualTo("arjun@test.com");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("createStudent: throws when email already exists")
    void createStudent_duplicateEmail_throws() {
        StudentRequest req = new StudentRequest("Arjun", "arjun@test.com", "CS001", "Anna University", "password123");
        when(studentRepository.existsByEmail("arjun@test.com")).thenReturn(true);

        assertThatThrownBy(() -> service.createStudent(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("createStudent: throws when roll number already exists")
    void createStudent_duplicateRoll_throws() {
        StudentRequest req = new StudentRequest("Arjun", "arjun@test.com", "CS001", "Anna University", "password123");
        when(studentRepository.existsByEmail(any())).thenReturn(false);
        when(studentRepository.existsByRollNumber("CS001")).thenReturn(true);

        assertThatThrownBy(() -> service.createStudent(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Roll number already exists");
    }

    @Test
    @DisplayName("getStudent: throws when student not found")
    void getStudent_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStudent(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    @DisplayName("recalculateSemesterGpa: weighted average is correct")
    void recalculateSemesterGpa_correct() {
        UUID semId = UUID.randomUUID();
        Semester semester = Semester.builder().id(semId).semesterNumber(1).build();

        Course c1 = Course.builder().id(UUID.randomUUID())
                .courseName("Maths").creditHours(4).semester(semester).build();
        Course c2 = Course.builder().id(UUID.randomUUID())
                .courseName("Physics").creditHours(3).semester(semester).build();

        Grade g1 = Grade.builder().gradePoints(9.0).build();
        Grade g2 = Grade.builder().gradePoints(8.0).build();

        when(semesterRepository.findById(semId)).thenReturn(Optional.of(semester));
        when(courseRepository.findBySemesterId(semId)).thenReturn(List.of(c1, c2));
        when(gradeRepository.findByCourseId(c1.getId())).thenReturn(Optional.of(g1));
        when(gradeRepository.findByCourseId(c2.getId())).thenReturn(Optional.of(g2));
        when(semesterRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.recalculateSemesterGpa(semId);

        ArgumentCaptor<Semester> captor = ArgumentCaptor.forClass(Semester.class);
        verify(semesterRepository).save(captor.capture());
        assertThat(captor.getValue().getGpa()).isEqualTo(8.57);
    }

    @Test
    @DisplayName("recalculateSemesterGpa: returns 0 when no grades exist")
    void recalculateSemesterGpa_noGrades_returnsZero() {
        UUID semId = UUID.randomUUID();
        Semester semester = Semester.builder().id(semId).build();
        Course c1 = Course.builder().id(UUID.randomUUID())
                .creditHours(4).semester(semester).build();

        when(semesterRepository.findById(semId)).thenReturn(Optional.of(semester));
        when(courseRepository.findBySemesterId(semId)).thenReturn(List.of(c1));
        when(gradeRepository.findByCourseId(any())).thenReturn(Optional.empty());
        when(semesterRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.recalculateSemesterGpa(semId);

        ArgumentCaptor<Semester> captor = ArgumentCaptor.forClass(Semester.class);
        verify(semesterRepository).save(captor.capture());
        assertThat(captor.getValue().getGpa()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("addOrUpdateGrade: upserts grade and triggers GPA recalculation")
    void addOrUpdateGrade_triggersRecalculation() {
        UUID courseId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();

        Semester semester = Semester.builder().id(semId).build();
        Course course = Course.builder()
                .id(courseId).creditHours(4).semester(semester).build();

        GradeRequest req = new GradeRequest(courseId, "A", 8.0);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(gradeRepository.findByCourseId(courseId)).thenReturn(Optional.empty());
        when(gradeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(marksHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(semesterRepository.findById(semId)).thenReturn(Optional.of(semester));
        when(courseRepository.findBySemesterId(semId)).thenReturn(List.of(course));
        when(gradeRepository.findByCourseId(courseId)).thenReturn(
                Optional.of(Grade.builder().gradePoints(8.0).build()));
        when(semesterRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Grade result = service.addOrUpdateGrade(req);

        assertThat(result.getLetterGrade()).isEqualTo("A");
        assertThat(result.getGradePoints()).isEqualTo(8.0);
        verify(semesterRepository, atLeastOnce()).save(any());
    }
}