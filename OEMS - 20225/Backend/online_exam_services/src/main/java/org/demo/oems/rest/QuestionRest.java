package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.QuestionBankInsertRequest;
import org.demo.oems.service.QuestionService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class QuestionRest {
    private final Logger logger = LogManager.getLogger(QuestionRest.class);

    private final QuestionService questionBankService;

    //=================================================================================================
    //Teacher Question Service API
    //=================================================================================================
    @Operation(summary = "Teacher Question Service - Get All Questions with Subject ID", description = "Get All Questions with Subject ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teacher/questions/{subjectId}")
    public ResponseEntity<Map<String, Object>> getQuestionBankBySubjectId(@PathVariable Long subjectId){
        try{
            logger.info("Start Retrieve all question bank by subject Id :: {}", subjectId );
            Map<String, Object> apiResponse = questionBankService.getAllQuestionsBySubject(subjectId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Get Question Bank By Subject ID", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Question Service - Get Questions by teacher ID", description = "Get All Questions with teacher ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teacher/questions/teacherId/{teacherId}")
    public ResponseEntity<Map<String, Object>> getQuestionBankBySubjectId(@PathVariable String teacherId){
        try{
            logger.info("Start - getQuestionBankByTeacherId Controller :: {}", teacherId);
            Map<String, Object> apiResponse = questionBankService.getAllQuestionsByTeacherID(teacherId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception - getQuestionBankByTeacherId Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("/teacher/question/{subjectId}")
    @Operation(summary = "Teacher Question Service", description = "Create New Question for subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> createQuestionsForSubject(@PathVariable long subjectId, @RequestBody List<QuestionBankInsertRequest> questionBankInsertRequest){
        try{
            logger.info("Start - createQuestionsForSubject Controller :: {}", questionBankInsertRequest);
            Map<String, Object> apiResponse = questionBankService.addQuestionToSubject(subjectId, questionBankInsertRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Teacher Question Service", description = "Update Question with Question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/teacher/question/{questionId}")
    public ResponseEntity<Map<String, Object>> updateQuestionsForSubject(@PathVariable long questionId, @RequestBody QuestionBankInsertRequest questionBankInsertRequest){
        try{
            logger.info("Start - updateQuestionsForSubject Controller :: {}", questionBankInsertRequest);
            Map<String, Object> apiResponse = questionBankService.editQuestionByQuestionId(questionId, questionBankInsertRequest);
            logger.info("End - updateQuestionsForSubject Controller :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception - updateQuestionsForSubject Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Question Service - Delete Question by ID", description = "Delete Both Question and Option with Question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/teacher/question/{questionId}")
    public ResponseEntity<Map<String, Object>> deleteQuestion(@PathVariable Long questionId){
        try{
            logger.debug("Start Delete Question ID :: {}", questionId);
            Map<String, Object> apiResponse = questionBankService.deleteQuestionById(questionId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Delete Question By ID", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Question Service - Get Question Summary Dashboard for Admin", description = "Question Summary Dashboard for Admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teacher/question/summary/{teacherId}")
    public ResponseEntity<Map<String, Object>> getQuestionSummaryDashboard(@PathVariable String teacherId){
        try {
            logger.debug("Start - getQuestionSummaryDashboard Controller {}", teacherId);
            Map<String, Object> apiResponse = questionBankService.getQuestionSummaryDashboardByTeacherId(teacherId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getQuestionSummaryDashboard", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

//    @Operation(summary = "Teacher Question Service", description = "Import Question bulk with excel")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successful"),
//            @ApiResponse(responseCode = "500", description = "Internal Server Error")
//    })
//    @PostMapping(value = "/teacher/questions/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Map<String, Object>> importQuestionInBulk(@RequestParam("file") MultipartFile file, @RequestParam("subjectId") Long subjectId){
//        try {
//            logger.debug("Start import question csv for subject {}", subjectId);
//            QuestionImportResponse questionImportResponse = questionBankService.importQuestions(file, subjectId, "");
//            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("0", "success", questionImportResponse);
//            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
//        }catch (Exception e){
//            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Delete Question By ID", e.getMessage());
//            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
//            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
//        }
//    }

    @Operation(summary = "Admin Question Service - Get Question Summary Dashboard for Admin", description = "Question Summary Dashboard for Admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/admin/question/summary")
    public ResponseEntity<Map<String, Object>> adminGetQuestionSummaryDashboard(){
        try {
            logger.debug("Start - getQuestionSummaryDashboard Controller {}", "Admin");
            Map<String, Object> apiResponse = questionBankService.getOverallQuestionSummaryDashboard();
            logger.info("End - getQuestionSummaryDashboard Controller {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getQuestionSummaryDashboard", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Question Service - Get All Questions with Subject ID", description = "Get All Questions with Subject ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/admin/questions/{subjectId}")
    public ResponseEntity<Map<String, Object>> adminGetQuestionBankBySubjectId(@PathVariable Long subjectId){
        try{
            logger.info("Start Retrieve all question bank by subject Id :: {}", subjectId );
            Map<String, Object> apiResponse = questionBankService.getAllQuestionsBySubject(subjectId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Get Question Bank By Subject ID", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("/admin/question/{subjectId}")
    @Operation(summary = "Admin Question Service - Create New Question", description = "Create New Question for subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> adminCreateQuestionsForSubject(@PathVariable long subjectId, @RequestBody List<QuestionBankInsertRequest> questionBankInsertRequest){
        try{
            logger.info("Add Question Bank :: {}", questionBankInsertRequest);
            Map<String, Object> apiResponse = questionBankService.addQuestionToSubject(subjectId, questionBankInsertRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Question Service - Update Question by ID", description = "Update Question with Question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/admin/question/{questionId}")
    public ResponseEntity<Map<String, Object>> adminUpdateQuestionsForSubject(@PathVariable long questionId, @RequestBody QuestionBankInsertRequest questionBankInsertRequest){
        try{
            logger.info("Add Question Bank :: {}", questionBankInsertRequest);
            Map<String, Object> apiResponse = questionBankService.editQuestionByQuestionId(questionId, questionBankInsertRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Question Service - Delete Question by ID", description = "Delete Both Question and Option with Question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/admin/question/{questionId}")
    public ResponseEntity<Map<String, Object>> adminDeleteQuestion(@PathVariable Long questionId){
        try{
            logger.debug("Start Delete Question ID :: {}", questionId);
            Map<String, Object> apiResponse = questionBankService.deleteQuestionById(questionId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Delete Question By ID", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

//    @Operation(summary = "Admin Question Service - Import Question Bulk with excel", description = "Import Question bulk with excel")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successful"),
//            @ApiResponse(responseCode = "500", description = "Internal Server Error")
//    })
//    @PostMapping(value = "/admin/questions/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Map<String, Object>> adminImportQuestionInBulk(@RequestParam("file") MultipartFile file, @RequestParam("subjectId") Long subjectId){
//        try {
//            logger.debug("Start import question csv for subject {}", subjectId);
//            QuestionImportResponse questionImportResponse = questionBankService.importQuestions(file, subjectId, "");
//            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("0", "success", questionImportResponse);
//            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
//        }catch (Exception e){
//            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Delete Question By ID", e.getMessage());
//            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
//            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
//        }
//    }

}
