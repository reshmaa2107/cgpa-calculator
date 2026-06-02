package com.cgpa.calculator.repository;

import com.cgpa.calculator.model.MarksHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface MarksHistoryRepository extends JpaRepository<MarksHistory, UUID> {
    List<MarksHistory> findByStudentIdOrderByRecordedAtDesc(UUID studentId);
    List<MarksHistory> findByStudentIdAndSemesterNoOrderByRecordedAtDesc(UUID studentId, Integer semesterNo);
}