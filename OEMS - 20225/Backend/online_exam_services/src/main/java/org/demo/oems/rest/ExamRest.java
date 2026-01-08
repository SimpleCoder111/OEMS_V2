package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ExamDomain;
import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.repository.ExamRepo;
import org.demo.oems.service.ExamService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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






}
