package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.CreateExamRequest;
import org.demo.oems.payload.request.ExamViolation;
import org.demo.oems.payload.request.TakeExamRequest;
import org.demo.oems.payload.response.ExamPaperResponse;
import org.demo.oems.service.ExamService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ExamRest {
    private final Logger logger = LogManager.getLogger(ExamRest.class);

    private final ExamService examService;

    public ExamRest(ExamService examService) {
        this.examService = examService;
    }

    //=============================================
    //Admin Exam Service - Start
    //==============================================
    @Operation(summary = "Admin Exam Service", description = "Get All Exams")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/admin/exams")
    public ResponseEntity<Map<String, Object>> getAllExams() {
        try {
            logger.info("Start - getAllExams Controller");
            Map<String, Object> apiResponse = examService.getAllExams();
            logger.info("End - getAllExams Controller :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - getAllExams Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Exam Service - Create New Exam", description = "Create New Exam by Admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/admin/exam")
    public ResponseEntity<Map<String, Object>> adminCreateExam(@RequestBody CreateExamRequest createExamRequest) {
        try {
            logger.info("Start - adminCreateExam :: {}", createExamRequest);
            Map<String, Object> apiResponse = examService.createExam(createExamRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - adminCreateExam Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Exam Service - Update an Exam", description = "Update an Exam by Exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/admin/exam/{examId}")
    public ResponseEntity<Map<String, Object>> adminUpdateExamInfo(@PathVariable long examId, @RequestBody CreateExamRequest createExamRequest) {
        try {
            logger.info("Start adminUpdateExamInfo :: {}", createExamRequest);
            Map<String, Object> apiResponse = examService.updateExam(examId, createExamRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - adminUpdateExamInfo Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Exam Service - Delete an Exam", description = "Delete an Exam by Exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/admin/exam/{examId}")
    public ResponseEntity<Map<String, Object>> adminDeleteExam(@PathVariable long examId) {
        try {
            logger.info("Start adminDeleteExam :: {}", examId);
            Map<String, Object> apiResponse = examService.deleteExam(examId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - adminDeleteExam Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Exam Service - Get Exam Details", description = "Get Exam Details by Exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/admin/exam/{examId}")
    public ResponseEntity<Map<String, Object>> adminGetSingleExamInfo(@PathVariable long examId) {
        try {
            logger.info("Start adminGetSingleExamInfo :: {}", examId);
            Map<String, Object> apiResponse = examService.getSingleExamInfoDetail(examId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - adminGetSingleExamInfo Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse =
                    ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    //=============================================
    //Teacher Exam Service - Start
    //==============================================
    @Operation(summary = "Teacher Exam Service - Set up Exam", description = "Create New Exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/teacher/exam")
    public ResponseEntity<Map<String, Object>> createExam(@RequestBody CreateExamRequest examCreateRequest) {
        try {
            logger.info("Start createExam Controller :: {}", examCreateRequest);
            Map<String, Object> apiResponse = examService.createExam(examCreateRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - createExam Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Exam Service - Update Exam Info", description = "Update Exam by Exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/teacher/exam/{examId}")
    public ResponseEntity<Map<String, Object>> updateExam(@PathVariable long examId, @RequestBody CreateExamRequest examUpdateRequest) {
        try {
            logger.info("Start - updateExam Controller :: {}", examUpdateRequest);
            Map<String, Object> apiResponse = examService.updateExam(examId, examUpdateRequest);
            logger.info("End - updateExam Controller :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - updateExam Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Exam Service", description = "Delete Exam by Exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/teacher/exam/{examId}")
    public ResponseEntity<Map<String, Object>> deleteExam(@PathVariable Long examId) {
        try {
            logger.debug("Start - deleteExam Controller :: {}", examId);
            Map<String, Object> apiResponse = examService.deleteExam(examId);
            logger.info("End - deleteExam Controller :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - deleteExam Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Exam Service", description = "Get Exam by Teacher ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teacher/exams/{teacherId}")
    public ResponseEntity<Map<String, Object>> getAllExamsByTeacherId(@PathVariable String teacherId) {
        try {
            logger.info("Start - getAllExamsByTeacherId Controller :: {}", teacherId);
            Map<String, Object> apiResponse = examService.getAllExamsByTeacherId(teacherId);
            logger.info("End - getAllExamsByTeacherId Controller :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - getAllExamsByTeacherId Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    //=============================================
    //Student Exam Service - Start
    //==============================================
    @Operation(summary = "Student Exam Service - Get Exam by Student ID", description = "Get Exam by Student ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/student/exams/{studentId}")
    public ResponseEntity<Map<String, Object>> getAllExamsByStudentId(@PathVariable String studentId) {
        try {
            logger.info("Start - getAllExamsByStudentId Controller :: {}", studentId);
            Map<String, Object> apiResponse = examService.getAllExamsByStudentId(studentId);
            logger.info("End - getAllExamsByStudentId Controller :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - getAllExamsByStudentId Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Exam Service - Take Exam", description = "Get Exam Paper")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/student/exam/take-exam")
    public ResponseEntity<Map<String, Object>> getExamPaperForStudent(@RequestBody TakeExamRequest takeExamRequest){
        try{
            logger.info("Start - getExamPaperForStudent Controller :: {}", takeExamRequest);
            Map<String, Object> apiResponse = examService.getExamPaperForStudent(takeExamRequest);
            logger.info("End - getExamPaperForStudent Controller :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception - getExamPaperForStudent Controller {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Exam Service - Save Progress", description = "Save Exam Paper Progress")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/student/exam/save-progress")
    public ResponseEntity<Map<String, Object>> saveExamPaperProgress(@RequestBody ExamPaperResponse examPaperSaveProgressRequest){
        Map<String, Object> apiResponse = new HashMap<>();
        try{
            logger.info("Start - saveExamPaperProgress Controller :: {}", examPaperSaveProgressRequest);
            apiResponse = examService.saveExamPaperProgress(examPaperSaveProgressRequest);
            logger.info("End - saveExamPaperProgress Controller :: {}", examPaperSaveProgressRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception - saveExamPaperProgress Controller :: {}", e.getMessage());
            apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Exam Service - Submit Exam Paper", description = "Submit Exam Paper")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/student/exam/submit")
    public ResponseEntity<Map<String, Object>> submitExamPaper(@RequestBody ExamPaperResponse examPaperSubmitRequest){
        try{
            logger.info("Start - submitExamPaper Controller :: {}", examPaperSubmitRequest);
            Map<String, Object> apiResponse = examService.submitExam(examPaperSubmitRequest);
            logger.info("End - submitExamPaper Controller :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception - submitExamPaper Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Exam Service - Save Student Exam Violation Count", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/student/exam/violation")
    public ResponseEntity<Map<String, Object>> studentViolationCount(@RequestBody ExamViolation examViolation){
        try{
            logger.info("Start - studentViolationCount Controller :: {}", examViolation);
            Map<String, Object> apiResponse = examService.examViolationCount(examViolation);
            logger.info("End - studentViolationCount Controller :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception - studentViolationCount Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }
}
