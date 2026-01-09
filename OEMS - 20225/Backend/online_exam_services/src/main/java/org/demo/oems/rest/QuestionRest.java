package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.payload.request.QuestionBankInsertRequest;
import org.demo.oems.payload.response.QuestionBankListsResponse;
import org.demo.oems.service.QuestionBankService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuestionRest {
    private final Logger logger = LogManager.getLogger(QuestionRest.class);

    private final QuestionBankService questionBankService;

    public QuestionRest(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    @GetMapping("/getQuestionBankBySubject")
    public ResponseEntity<?> getQuestionBank(@RequestParam("subjectId") long subjectId){
        try{
            logger.info("Start Retrieve all question bank by subject Id :: {}", subjectId );
            JSONObject apiResponse = questionBankService.getAllQuestionBanksBySubject(subjectId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("/addQuestionBank")
    public ResponseEntity<?> addQuestionBank(@RequestBody QuestionBankInsertRequest questionBankInsertRequest){
        try{
            logger.info("Add Question Bank :: {}", questionBankInsertRequest);
            JSONObject apiResponse = questionBankService.addQuestionBanks(questionBankInsertRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("/addQuestionBanksAsArray")
    public ResponseEntity<?> addQuestionBanks(@RequestBody List<QuestionBankInsertRequest> apiRequest){
        try{
            logger.info("Add Question Bank as Array :: {}", apiRequest);
            JSONObject apiResponse = questionBankService.addQuestionBanksArray(apiRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }
}
