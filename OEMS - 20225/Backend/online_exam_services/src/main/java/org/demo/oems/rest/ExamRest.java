package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.CreateExamRequest;
import org.demo.oems.payload.request.ExamPaperGenerationRequest;
import org.demo.oems.service.ExamService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exams")
public class ExamRest {
    private final Logger logger = LogManager.getLogger(ExamRest.class);

    private final ExamService examService;

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

    @Operation(summary = "Teacher Exam Service", description = "Create New Exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createExam(@RequestBody CreateExamRequest examCreateRequest) {
        try {
            logger.info("Start Create Exam :: {}", examCreateRequest);
            Map<String, Object> apiResponse = examService.createExam(examCreateRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,
                    "Create Exam", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Exam Service", description = "Update Exam by Exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("")
    public ResponseEntity<Map<String, Object>> updateExam(@RequestBody CreateExamRequest examUpdateRequest) {
        try {
            logger.info("Start Update Exam info with request :: {}", examUpdateRequest);
            Map<String, Object> apiResponse = examService.updateExam(examUpdateRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,
                    "Update Exam", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Exam Service", description = "Delete Exam by Exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/{examId}")
    public ResponseEntity<Map<String, Object>> deleteExam(@PathVariable Long examId) {
        try {
            logger.debug("Start Delete Exam ID :: {}", examId);
            Map<String, Object> apiResponse = examService.deleteExam(examId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,
                    "Delete Exam", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Exam Service", description = "Get All Exams")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExams() {
        try {
            logger.info("Start Retrieve All Exams");
            Map<String, Object> apiResponse = examService.getAllExams();
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,
                    "Get All Exams", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Exam Service", description = "Get Exam by Teacher ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/{teacherId}")
    public ResponseEntity<Map<String, Object>> getAllExamsByTeacherId(@PathVariable String teacherId) {
        try {
            logger.info("Start Retrieve Exam Lists By Teacher ID :: {}", teacherId);
            Map<String, Object> apiResponse = examService.getAllExamsByTeacherId(teacherId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,
                    "Get Exam By ID", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }











}
