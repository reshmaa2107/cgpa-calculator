package com.cgpa.calculator.security;

import com.cgpa.calculator.model.Student;
import com.cgpa.calculator.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new User(
                student.getEmail(),
                student.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + student.getRole().name()))
        );
    }
}