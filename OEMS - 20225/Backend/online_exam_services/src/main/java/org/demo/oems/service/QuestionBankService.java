package org.demo.oems.service;

import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.repository.QuestionBankRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionBankService {
    private final QuestionBankRepo questionBankRepo;

    public QuestionBankService(QuestionBankRepo questionBankRepo) {
        this.questionBankRepo = questionBankRepo;
    }

    public List<QuestionBankDomain> getAllQuestionBanksBySubject(long subjectId){
        return questionBankRepo.getQuestionBankDomainsBySubjectId(subjectId);
    }
}
