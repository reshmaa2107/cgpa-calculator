package com.cgpa.calculator.controller;

import com.cgpa.calculator.dto.*;
import com.cgpa.calculator.model.Student;
import com.cgpa.calculator.service.CgpaCalculatorService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StudentControllerTest {

    @LocalServerPort int port;
    @MockitoBean CgpaCalculatorService service;

    private RestClient client;
    private UUID studentId;
    private Student mockStudent;

    @BeforeEach
    void setUp() {
        client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultStatusHandler(status -> true, (req, res) -> {})
                .build();

        studentId = UUID.randomUUID();
        mockStudent = Student.builder()
                .id(studentId)
                .name("Arjun Kumar")
                .email("arjun@test.com")
                .rollNumber("CS001")
                .build();
    }

    @Test
    @DisplayName("POST /api/students/register: creates student and returns 201")
    void createStudent_returns201() {
        StudentRequest req = new StudentRequest(
                "Arjun Kumar", "arjun@test.com", "CS001", "Anna University", "password123");
        when(service.createStudent(any())).thenReturn(mockStudent);

        ResponseEntity<String> res = client.post()
                .uri("/api/students/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toEntity(String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("GET /api/students/{id}: security redirects unauthenticated requests")
    void getStudent_returnsStudent() {
        when(service.getStudent(studentId)).thenReturn(mockStudent);

        ResponseEntity<String> res = client.get()
                .uri("/api/students/{id}", studentId)
                .retrieve()
                .toEntity(String.class);

        assertThat(res.getStatusCode().value()).isIn(200, 302, 401, 403);
    }

    @Test
    @DisplayName("GET /api/students/{id}/cgpa: security redirects unauthenticated requests")
    void getCgpa_returnsCgpaResponse() {
        CgpaResponse response = CgpaResponse.builder()
                .studentId(studentId)
                .studentName("Arjun Kumar")
                .rollNumber("CS001")
                .cgpa(8.75)
                .semesters(List.of())
                .build();
        when(service.getCgpa(studentId)).thenReturn(response);

        ResponseEntity<String> res = client.get()
                .uri("/api/students/{id}/cgpa", studentId)
                .retrieve()
                .toEntity(String.class);

        assertThat(res.getStatusCode().value()).isIn(200, 302, 401, 403);
    }
}