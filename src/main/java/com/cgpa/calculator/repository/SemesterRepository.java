package com.cgpa.calculator.repository;

import com.cgpa.calculator.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, UUID> {
    List<Semester> findByStudentIdOrderBySemesterNumberAsc(UUID studentId);

    @Query("SELECT AVG(s.gpa) FROM Semester s WHERE s.student.id = :studentId AND s.gpa IS NOT NULL")
    Optional<Double> calculateCgpaByStudentId(UUID studentId);
}