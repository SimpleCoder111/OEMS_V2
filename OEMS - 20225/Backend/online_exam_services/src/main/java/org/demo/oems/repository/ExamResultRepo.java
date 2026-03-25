package org.demo.oems.repository;

import org.demo.oems.domain.ExamResultDomain;
import org.demo.oems.domain.SubjectRankingProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepo extends JpaRepository<ExamResultDomain, Long> {

   List<ExamResultDomain> findExamResultDomainByStudentId(String studentId);

   Optional<ExamResultDomain> findExamResultDomainByStudentIdAndExam_Id(String studentId, long examId);

   List<ExamResultDomain> findExamResultDomainByExam_Id(long examId);

   List<ExamResultDomain> findByExamIdOrderByScoreDesc(long examId);

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

   @Query(value = """
     SELECT student_id, total_score, total_time_taken,
            RANK() OVER (ORDER BY total_score DESC, total_time_taken ASC) as final_rank
     FROM (
         SELECT r.student_id, 
                SUM(r.score) as total_score,
                SUM(r.time_taken) as total_time_taken
         FROM exam_result r
         JOIN exam_info i ON r.exam_id = i.id
         WHERE i.subject_id = :subjectId AND r.status = 'GRADED'
         GROUP BY r.student_id
     ) subquery
     """, nativeQuery = true)
   List<SubjectRankingProjection> findSubjectRankingAcrossClass(@Param("subjectId") long subjectId);


   @Query(value = """
     SELECT student_id, total_score, total_time_taken,
            RANK() OVER (ORDER BY total_score DESC, total_time_taken ASC) as final_rank
     FROM (
         SELECT r.student_id, 
                SUM(r.score) as total_score,
                SUM(r.time_taken) as total_time_taken
         FROM exam_result r
         JOIN exam_info i ON r.exam_id = i.id
         WHERE i.subject_id = :subjectId 
         AND i.class_id = :classId
         AND r.status = 'GRADED'
         GROUP BY r.student_id
     ) subquery
     """, nativeQuery = true)
   List<SubjectRankingProjection> findSubjectRankingInClass(@Param("subjectId") long subjectId, @Param("classId") long classId);

}
