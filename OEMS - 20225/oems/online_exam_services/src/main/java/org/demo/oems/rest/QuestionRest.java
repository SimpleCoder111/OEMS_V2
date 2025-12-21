package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.service.QuestionBankService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuestionRest {
    private final Logger logger = LogManager.getLogger(QuestionRest.class);

    private final QuestionBankService questionBankService;

    public QuestionRest(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    @GetMapping("/teacher/getQuestionBankBySubject")
    public ResponseEntity<?> getQuestionBank(@RequestParam("subjectId") long subjectId){
        try{
            logger.info("Start Retrieve all question bank by subject Id :: {}", subjectId );
            List<QuestionBankDomain> questionBankList = questionBankService.getAllQuestionBanksBySubject(subjectId);
            return new ResponseEntity<>(questionBankList, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }
}
