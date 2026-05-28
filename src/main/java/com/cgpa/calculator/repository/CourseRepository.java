package com.cgpa.calculator.repository;

import com.cgpa.calculator.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findBySemesterId(UUID semesterId);
}