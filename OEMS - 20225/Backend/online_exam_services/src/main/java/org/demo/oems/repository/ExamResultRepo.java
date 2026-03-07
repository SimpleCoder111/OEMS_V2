package org.demo.oems.repository;

import org.demo.oems.domain.ExamResultDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamResultRepo extends JpaRepository<ExamResultDomain, Long> {

   List<ExamResultDomain> findExamResultDomainByStudentId(String studentId);

   List<ExamResultDomain> findExamResultDomainByExam_Id(long examId);



}
