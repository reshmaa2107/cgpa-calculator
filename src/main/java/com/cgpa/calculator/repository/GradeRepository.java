package com.cgpa.calculator.repository;

import com.cgpa.calculator.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {
    Optional<Grade> findByCourseId(UUID courseId);
}