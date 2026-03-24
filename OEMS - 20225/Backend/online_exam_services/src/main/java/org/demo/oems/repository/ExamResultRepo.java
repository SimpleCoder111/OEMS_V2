package org.demo.oems.repository;

import org.demo.oems.domain.ExamResultDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExamResultRepo extends JpaRepository<ExamResultDomain, Long> {

   List<ExamResultDomain> findExamResultDomainByStudentId(String studentId);

   Optional<ExamResultDomain> findExamResultDomainByStudentIdAndExam_Id(String studentId, long examId);

   List<ExamResultDomain> findExamResultDomainByExam_Id(long examId);

   @Query(value = """
        SELECT 
            CASE 
                WHEN r.score >= 90 THEN 'A'
                WHEN r.score >= 80 THEN 'B'
                WHEN r.score >= 70 THEN 'C'
                WHEN r.score >= 60 THEN 'D'
                WHEN r.score >= 50 THEN 'E'
                ELSE 'F'
            END as grade,
            COUNT(*) as count
        FROM exam_result r
        GROUP BY grade
        ORDER BY grade ASC
        """, nativeQuery = true)
   List<GradeCountProjection> findOverallGradeDistribution();

}
