package com.cgpa.calculator.controller;

import com.cgpa.calculator.model.Student;
import com.cgpa.calculator.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final StudentRepository studentRepository;

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register() { return "register"; }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails u, Model model) {
        addStudent(u, model);
        return "home";
    }

    @GetMapping("/calculate-gpa")
    public String calculateGpa(@AuthenticationPrincipal UserDetails u, Model model) {
        addStudent(u, model);
        return "calculate-gpa";
    }

    @GetMapping("/calculate-cgpa")
    public String calculateCgpa(@AuthenticationPrincipal UserDetails u, Model model) {
        addStudent(u, model);
        return "calculate-cgpa";
    }

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal UserDetails u, Model model) {
        addStudent(u, model);
        return "history";
    }

    @GetMapping("/analysis")
    public String analysis(@AuthenticationPrincipal UserDetails u, Model model) {
        addStudent(u, model);
        return "analysis";
    }

    private void addStudent(UserDetails u, Model model) {
        Student s = studentRepository.findByEmail(u.getUsername()).orElseThrow();
        model.addAttribute("studentId", s.getId());
        model.addAttribute("studentName", s.getName());
        model.addAttribute("studentEmail", s.getEmail());
        model.addAttribute("college", s.getCollege());
    }
}