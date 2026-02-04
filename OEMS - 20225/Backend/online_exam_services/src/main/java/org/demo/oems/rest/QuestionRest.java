package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.QuestionBankInsertRequest;
import org.demo.oems.payload.response.QuestionImportResponse;
import org.demo.oems.service.QuestionBankService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionRest {
    private final Logger logger = LogManager.getLogger(QuestionRest.class);

    private final QuestionBankService questionBankService;

    public QuestionRest(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    @Operation(summary = "Teacher Question Service", description = "Get All Questions with Subject ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/{subjectId}")
    public ResponseEntity<Map<String, Object>> getQuestionBankBySubjectId(@PathVariable Long subjectId){
        try{
            logger.info("Start Retrieve all question bank by subject Id :: {}", subjectId );
            Map<String, Object> apiResponse = questionBankService.getAllQuestionBanksBySubject(subjectId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Get Question Bank By Subject ID", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("/{subjectId}")
    @Operation(summary = "Teacher Question Service", description = "Create New Question for subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> createQuestionsForSubject(@PathVariable long subjectId, @RequestBody QuestionBankInsertRequest questionBankInsertRequest){
        try{
            logger.info("Add Question Bank :: {}", questionBankInsertRequest);
            Map<String, Object> apiResponse = questionBankService.addQuestionBanks(subjectId, questionBankInsertRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Question Service", description = "Update Question with Question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/{questionId}")
    public ResponseEntity<Map<String, Object>> updateQuestionsForSubject(@PathVariable long questionId, @RequestBody QuestionBankInsertRequest questionBankInsertRequest){
        try{
            logger.info("Add Question Bank :: {}", questionBankInsertRequest);
            Map<String, Object> apiResponse = questionBankService.editQuestionByQuestionId(questionId, questionBankInsertRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Question Service", description = "Delete Both Question and Option with Question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Map<String, Object>> deleteQuestion(@PathVariable Long questionId){
        try{
            logger.debug("Start Delete Question ID :: {}", questionId);
            Map<String, Object> apiResponse = questionBankService.deleteQuestionAndOptionBank(questionId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Delete Question By ID", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Question Service", description = "Import Question bulk with excel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
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

    @Operation(summary = "Teacher Question Service", description = "Question Summary Dashboard for Teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/summary/{teacherId}")
    public ResponseEntity<Map<String, Object>> getQuestionSummaryDashboard(@PathVariable String teacherId){
        try {
            logger.debug("Start - getQuestionSummaryDashboard Controller  {}", teacherId);
            Map<String, Object> apiResponse = questionBankService.getQuestionSummaryDashboard(teacherId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Delete Question By ID", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

}
