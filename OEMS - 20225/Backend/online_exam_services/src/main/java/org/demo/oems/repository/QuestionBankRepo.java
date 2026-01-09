package org.demo.oems.repository;

import org.demo.oems.domain.QuestionBankDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionBankRepo extends JpaRepository<QuestionBankDomain, Long> {

    List<QuestionBankDomain> getQuestionBankDomainsBySubjectId(long subjectId);

    @Query(value = "SELECT * FROM questions WHERE difficulty = :difficulty AND subjectId = :subjectId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<QuestionBankDomain> findRandomNativeByDifficultyAndSubjectId(@Param("difficulty") String difficulty, @Param("limit") int limit, @Param("subjectId") long subjectId);

}
