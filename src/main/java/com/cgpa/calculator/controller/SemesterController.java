package com.cgpa.calculator.controller;

import com.cgpa.calculator.model.Semester;
import com.cgpa.calculator.service.CgpaCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final CgpaCalculatorService service;


    @PostMapping
    public ResponseEntity<Semester> addSemester(
            @RequestParam UUID studentId,
            @RequestParam Integer semesterNumber,
            @RequestParam(required = false) String academicYear) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addSemester(studentId, semesterNumber, academicYear));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Semester>> getSemestersByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(service.getSemestersByStudent(studentId));
    }
}