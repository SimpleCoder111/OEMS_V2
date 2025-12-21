package org.demo.oems.repository;

import org.demo.oems.domain.QuestionBankDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionBankRepo extends JpaRepository<QuestionBankDomain, Long> {

    List<QuestionBankDomain> getQuestionBankDomainsBySubjectId(long subjectId);

}
