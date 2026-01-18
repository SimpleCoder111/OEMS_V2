package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.ExamPaperGenerationRequest;
import org.demo.oems.service.ExamService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ExamRest {
    private final Logger logger = LogManager.getLogger(ExamRest.class);

    private ExamService examService;

    public ExamRest(ExamService examService) {
        this.examService = examService;
    }


    @GetMapping("/getExamPaper")
    public ResponseEntity<?> getRandomizeExamQuestions(@RequestBody ExamPaperGenerationRequest apiRequest){
        try{
            logger.info("Start Get Randomized Exam Questions :: {}", apiRequest);
            JSONObject apiResponse = examService.getExamPaper(apiRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while Get Randomized Exam Questions :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }






}
