package org.demo.oems.repository;

import org.demo.oems.domain.ExamDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ExamRepo extends JpaRepository<ExamDomain, Long> {
    // ExamRepository (assuming GeneratedExam or Exam entity)
    long countByExamDateBetween(LocalDateTime start, LocalDateTime end);

}
