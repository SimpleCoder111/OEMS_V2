package org.demo.oems.rest;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.QuestionBankInsertRequest;
import org.demo.oems.payload.response.QuestionImportResponse;
import org.demo.oems.repository.QuestionBankRepo;
import org.demo.oems.service.QuestionBankService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionRest {
    private final Logger logger = LogManager.getLogger(QuestionRest.class);

    private final QuestionBankService questionBankService;
    private final QuestionBankRepo questionBankRepo;

    public QuestionRest(QuestionBankService questionBankService,
                        QuestionBankRepo questionBankRepo) {
        this.questionBankService = questionBankService;
        this.questionBankRepo = questionBankRepo;
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<?> getQuestionBankBySubjectId(@PathVariable Long subjectId){
        try{
            logger.info("Start Retrieve all question bank by subject Id :: {}", subjectId );
            JSONObject apiResponse = questionBankService.getAllQuestionBanksBySubject(subjectId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Get Question Bank By Subject ID", e.getMessage());
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


    /*Create Single Question for subject*/
    @PostMapping("/")
    public ResponseEntity<?> createQuestionsForSubject(@RequestBody QuestionBankInsertRequest questionBankInsertRequest){
        try{
            logger.info("Add Question Bank :: {}", questionBankInsertRequest);
            JSONObject apiResponse = questionBankService.addQuestionBanks(questionBankInsertRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    /*Update Single Question for subject*/
    @PutMapping("/")
    public ResponseEntity<?> updateQuestionsForSubject(@RequestBody QuestionBankInsertRequest questionBankInsertRequest){
        try{
            logger.info("Add Question Bank :: {}", questionBankInsertRequest);
            JSONObject apiResponse = questionBankService.addQuestionBanks(questionBankInsertRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    /*Delete Question*/
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Map<String, Object>> deleteQuestion(@PathVariable Long questionId){
        try{
            logger.debug("Start Delete Question ID :: {}", questionId);
            questionBankService.deleteQuestionAndOptionBank(questionId);
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("0", "successfully deleted !!!", "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Delete Question By ID", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importQuestionInBulk(@RequestParam("file") MultipartFile file, @RequestParam("subjectId") Long subjectId){
        try {
            logger.debug("Start import question csv for subject {}", subjectId);
            QuestionImportResponse questionImportResponse = questionBankService.importQuestions(file, subjectId, "");
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("0", "success", questionImportResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Delete Question By ID", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }


}
