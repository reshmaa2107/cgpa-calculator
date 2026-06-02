package com.cgpa.calculator.controller;

import com.cgpa.calculator.model.MarksHistory;
import com.cgpa.calculator.service.CgpaCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final CgpaCalculatorService service;

    @GetMapping("/{studentId}")
    public ResponseEntity<List<MarksHistory>> getHistory(@PathVariable UUID studentId) {
        return ResponseEntity.ok(service.getHistory(studentId));
    }
}