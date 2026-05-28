package com.cgpa.calculator.controller;

import com.cgpa.calculator.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final StudentRepository studentRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "index";
    }

    @GetMapping("/student/{id}")
    public String studentDashboard(@PathVariable UUID id, Model model) {
        model.addAttribute("studentId", id);
        return "dashboard";
    }
}