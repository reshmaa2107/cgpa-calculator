package com.cgpa.calculator.controller;

import com.cgpa.calculator.dto.*;
import com.cgpa.calculator.model.*;
import com.cgpa.calculator.service.CgpaCalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final CgpaCalculatorService service;

    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody StudentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStudent(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getStudent(id));
    }

    @GetMapping("/{id}/cgpa")
    public ResponseEntity<CgpaResponse> getCgpa(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getCgpa(id));
    }

    @PostMapping("/register")
    public ResponseEntity<Student> register(@Valid @RequestBody StudentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStudent(req));
    }

}