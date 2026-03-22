package org.demo.oems.repository;

import org.demo.oems.domain.QuestionBankDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionBankRepo extends JpaRepository<QuestionBankDomain, Long> {

    List<QuestionBankDomain> getQuestionBankDomainsBySubjectId(Long subjectId);

    @Query(value = "SELECT * FROM question_bank WHERE difficulty = :difficulty AND subject_id = :subjectId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<QuestionBankDomain> findRandomNativeByDifficultyAndSubjectId(@Param("difficulty") String difficulty, @Param("limit") int limit, @Param("subjectId") long subjectId);

    long countBySubject_Id(Long subjectId);

    long countQuestionBankDomainsByDifficulty(String difficulty);

    long countQuestionBankDomainsByQuestionTypeEqualsIgnoreCase(String questionType);

    long countBySubject_IdAndDifficulty(long subjectId, String difficulty);

    long countBySubject_IdAndQuestionType(long subjectId, String questionType);

    long countAllBySubject_Id(long subjectId);

    List<QuestionBankDomain> findQuestionBankDomainsBySubject_Id(long subjectId);

    List<QuestionBankDomain> findQuestionBankDomainsBySubject_IdOrderByChapter_IdAsc(long subjectId);

    void deleteBySubject_Id(Long subjectId);

    long countByChapter_Id(Long chapterId);

    void deleteByChapter_Id(Long chapterId);

    List<QuestionBankDomain> findQuestionBankDomainsBySubject_IdOrderByChapter_Id(long subjectId);
}
