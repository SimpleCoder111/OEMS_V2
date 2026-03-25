package org.demo.oems.repository;

import org.demo.oems.domain.ExamDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExamRepo extends JpaRepository<ExamDomain, Long> {
    // ExamRepository (assuming GeneratedExam or Exam entity)
    long countByExamDateBetween(LocalDateTime start, LocalDateTime end);

    List<ExamDomain> getExamDomainsByClassIdAndSubjectId(long classId, long subjectId);

    List<ExamDomain> getExamDomainsBySubjectId(long subjectId);

    List<ExamDomain> getExamDomainsByClassId(long classId);

    List<ExamDomain> getExamDomainsByClassIdAndExamDateIsAfter(long classId, LocalDateTime localDateTime);

    Optional<ExamDomain> findFirstByClassIdAndExamDateAfterOrderByExamDateAsc(long classId, LocalDateTime localDateTime);
}
