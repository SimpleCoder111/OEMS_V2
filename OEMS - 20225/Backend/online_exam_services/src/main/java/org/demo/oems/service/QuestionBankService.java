package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.payload.request.QuestionBankInsertRequest;
import org.demo.oems.repository.QuestionBankRepo;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionBankService {
    private final Logger logger = LogManager.getLogger(QuestionBankService.class);
    private final QuestionBankRepo questionBankRepo;

    public QuestionBankService(QuestionBankRepo questionBankRepo) {
        this.questionBankRepo = questionBankRepo;
    }

    public List<QuestionBankDomain> getAllQuestionBanksBySubject(long subjectId){
        return questionBankRepo.getQuestionBankDomainsBySubjectId(subjectId);
    }

    public JSONObject addQuestionBanks(QuestionBankInsertRequest requestPayload){
        JSONObject addQuestionBankResponse = new JSONObject();
        String responseStatus;
        String responseMessage;
        try{
            QuestionBankDomain newQuestionBank = new QuestionBankDomain();
            newQuestionBank.setQuestionType(requestPayload.getQuestionType());
            newQuestionBank.setQuestionContent(requestPayload.getQuestionContent());
            newQuestionBank.setDifficulty(requestPayload.getDifficulty());
            newQuestionBank.setLangCode(requestPayload.getLangCode());
            newQuestionBank.setCreatedBy(requestPayload.getCreatedBy());
            newQuestionBank.setSubjectId(requestPayload.getSubjectId());

            questionBankRepo.save(newQuestionBank);

            responseStatus = "success";
            responseMessage = "Successfully Insert";

            addQuestionBankResponse.put("responseStatus", responseStatus);
            addQuestionBankResponse.put("responseMessage", responseMessage);



        }catch (Exception e){
            logger.error("Exception while add question banks :: {}" , e.getMessage() );
            responseStatus = "fail";
            responseMessage = e.getMessage();

            addQuestionBankResponse.put("responseStatus", responseStatus);
            addQuestionBankResponse.put("responseMessage", responseMessage);

        }

        return  addQuestionBankResponse;

    }
}
