package com.cgpa.calculator.controller;

import com.cgpa.calculator.dto.GradeRequest;
import com.cgpa.calculator.model.Grade;
import com.cgpa.calculator.service.CgpaCalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final CgpaCalculatorService service;

    @PostMapping
    public ResponseEntity<Grade> addOrUpdateGrade(@Valid @RequestBody GradeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addOrUpdateGrade(req));
    }
}