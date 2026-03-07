package org.demo.oems.repository;

import org.demo.oems.domain.ExamSessionDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamSessionRepo extends JpaRepository<ExamSessionDomain, Long> {
    Optional<ExamSessionDomain> findExamSessionDomainByExam_IdAndStudent_UserId(long examId, String userId);

}
