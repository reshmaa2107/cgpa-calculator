package com.cgpa.calculator.service;

import com.cgpa.calculator.dto.*;
import com.cgpa.calculator.model.*;
import com.cgpa.calculator.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CgpaCalculatorService {

    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final MarksHistoryRepository marksHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Student ──────────────────────────────────────

    public Student createStudent(StudentRequest req) {
        if (studentRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered");
        if (studentRepository.existsByRollNumber(req.getRollNumber()))
            throw new IllegalArgumentException("Roll number already exists");

        Student student = Student.builder()
                .name(req.getName())
                .email(req.getEmail())
                .rollNumber(req.getRollNumber())
                .college(req.getCollege())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Student.Role.STUDENT)
                .build();
        return studentRepository.save(student);
    }

    public Student getStudent(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found"));
    }

    // ── Semester ─────────────────────────────────────

    public Semester addSemester(UUID studentId, Integer semesterNumber, String academicYear) {
        Student student = getStudent(studentId);
        Semester semester = Semester.builder()
                .student(student)
                .semesterNumber(semesterNumber)
                .academicYear(academicYear)
                .build();
        return semesterRepository.save(semester);
    }

    public List<Semester> getSemestersByStudent(UUID studentId) {
        return semesterRepository.findByStudentIdOrderBySemesterNumberAsc(studentId);
    }

    // ── Course ───────────────────────────────────────

    public Course addCourse(UUID semesterId, String name, String code, Integer credits) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new NoSuchElementException("Semester not found"));
        Course course = Course.builder()
                .semester(semester)
                .courseName(name)
                .courseCode(code)
                .creditHours(credits)
                .build();
        return courseRepository.save(course);
    }

    public List<Course> getCoursesBySemester(UUID semesterId) {
        return courseRepository.findBySemesterId(semesterId);
    }

    // ── Grade & GPA calculation ───────────────────────

    @Transactional
    public Grade addOrUpdateGrade(GradeRequest req) {
        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new NoSuchElementException("Course not found"));

        // upsert — update if exists, else create
        Grade grade = gradeRepository.findByCourseId(course.getId())
                .orElse(Grade.builder().course(course).build());

        grade.setLetterGrade(req.getLetterGrade());
        grade.setGradePoints(req.getGradePoints());
        gradeRepository.save(grade);

        // save to marks history
        MarksHistory history = MarksHistory.builder()
                .student(course.getSemester().getStudent())
                .semesterNo(course.getSemester().getSemesterNumber())
                .courseName(course.getCourseName())
                .courseCode(course.getCourseCode())
                .creditHours(course.getCreditHours())
                .letterGrade(req.getLetterGrade())
                .gradePoints(req.getGradePoints())
                .build();
        marksHistoryRepository.save(history);

        // recalculate semester GPA
        recalculateSemesterGpa(course.getSemester().getId());

        return grade;
    }

    @Transactional
    public void recalculateSemesterGpa(UUID semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new NoSuchElementException("Semester not found"));

        List<Course> courses = courseRepository.findBySemesterId(semesterId);

        int totalCredits = 0;
        double weightedPoints = 0.0;

        for (Course c : courses) {
            Optional<Grade> grade = gradeRepository.findByCourseId(c.getId());
            if (grade.isPresent()) {
                totalCredits += c.getCreditHours();
                weightedPoints += grade.get().getGradePoints() * c.getCreditHours();
            }
        }

        Double gpa = totalCredits > 0
                ? Math.round((weightedPoints / totalCredits) * 100) / 100.0
                : 0.0;

        semester.setGpa(gpa);
        semesterRepository.save(semester);
    }

    // ── CGPA ─────────────────────────────────────────

    public CgpaResponse getCgpa(UUID studentId) {
        Student student = getStudent(studentId);
        List<Semester> semesters = semesterRepository
                .findByStudentIdOrderBySemesterNumberAsc(studentId);

        Double cgpa = semesterRepository.calculateCgpaByStudentId(studentId)
                .map(v -> Math.round(v * 100.0) / 100.0)
                .orElse(0.0);

        List<CgpaResponse.SemesterSummary> summaries = semesters.stream().map(s -> {
            List<Course> courses = courseRepository.findBySemesterId(s.getId());
            int totalCredits = courses.stream().mapToInt(Course::getCreditHours).sum();
            return CgpaResponse.SemesterSummary.builder()
                    .semesterNumber(s.getSemesterNumber())
                    .academicYear(s.getAcademicYear())
                    .gpa(s.getGpa())
                    .totalCredits(totalCredits)
                    .coursesCount(courses.size())
                    .build();
        }).collect(Collectors.toList());

        return CgpaResponse.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .rollNumber(student.getRollNumber())
                .cgpa(cgpa)
                .semesters(summaries)
                .build();
    }

    // ── History ──────────────────────────────────────

    public List<MarksHistory> getHistory(UUID studentId) {
        return marksHistoryRepository.findByStudentIdOrderByRecordedAtDesc(studentId);
    }
}