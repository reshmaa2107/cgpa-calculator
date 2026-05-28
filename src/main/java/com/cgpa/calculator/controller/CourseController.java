package com.cgpa.calculator.controller;

import com.cgpa.calculator.model.Course;
import com.cgpa.calculator.service.CgpaCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CgpaCalculatorService service;

    @PostMapping
    public ResponseEntity<Course> addCourse(
            @RequestParam UUID semesterId,
            @RequestParam String courseName,
            @RequestParam(required = false) String courseCode,
            @RequestParam Integer creditHours) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addCourse(semesterId, courseName, courseCode, creditHours));
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<List<Course>> getCoursesBySemester(@PathVariable UUID semesterId) {
        return ResponseEntity.ok(service.getCoursesBySemester(semesterId));
    }
}