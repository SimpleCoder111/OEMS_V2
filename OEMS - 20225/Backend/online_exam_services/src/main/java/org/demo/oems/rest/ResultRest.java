package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.demo.oems.payload.response.GradingDetailsResponse;
import org.demo.oems.service.ResultService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ResultRest {
    private static final Logger logger = LogManager.getLogger(ResultRest.class);

    private final ResultService resultService;

    //================================================================================================
    //Admin Results Service Sections
    //================================================================================================
    @Operation(summary = "Admin Services :: Get All Classes Result", description = "Get All Classes Result")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/admin/results")
    public ResponseEntity<Map<String, Object>> getAllClassesResult() {
        try {
            logger.debug("Start - getAllClassesResult API");
            Map<String, Object> getAllClassesByStudentIdResponse = resultService.getAllClassesResult();
            logger.debug("End - getAllClassesResult", getAllClassesByStudentIdResponse);
            return new ResponseEntity<>(getAllClassesByStudentIdResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesResult", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    //================================================================================================
    //Admin Results Service Sections
    //================================================================================================
    @Operation(summary = "Teacher Services :: Get Classes Result", description = "Get Classes Result by Class ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teacher/results/class/{classId}")
    public ResponseEntity<Map<String, Object>> getClassesResultByClassId(@PathVariable long classId) {
        try {
            logger.debug("Start - getClassesResultByClassId API :: {}", classId);
            Map<String, Object> getAllClassesByStudentIdResponse = resultService.getClassesResultByClassId(classId);
            logger.debug("Final API Response :: {}", getAllClassesByStudentIdResponse);
            return new ResponseEntity<>(getAllClassesByStudentIdResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getClassesResultByClassId", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Services :: Get Exam Result by Exam ID", description = "Get Exam Result by Exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teacher/results/exam/{examId}")
    public ResponseEntity<Map<String, Object>> getClassesResultByExamId(@PathVariable long examId) {
        try {
            logger.debug("Start - getClassesResultByExamId API :: {}", examId);
            Map<String, Object> getAllClassesByStudentIdResponse = resultService.getClassesResultByExamId(examId);
            logger.debug("Final API Response :: {}", getAllClassesByStudentIdResponse);
            return new ResponseEntity<>(getAllClassesByStudentIdResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getClassesResultByExamId", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Services :: Get Exam Result by Exam ID", description = "Get Exam Result by Exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teacher/result/grading-details")
    public ResponseEntity<Map<String, Object>> getGradingResultByExamIdAndStudentId(@RequestParam long examId, @RequestParam String studentId) {
        try {
            logger.debug("Start - getGradingResultByExamIdAndStudentId API :: examId :: {}, studentId :: {}", examId, studentId);
            Map<String, Object> getGradingResultByStudentId = resultService.getGradingResultByExamIdAndStudentId(examId, studentId);
            logger.debug("Final API Response :: {}", getGradingResultByStudentId);
            return new ResponseEntity<>(getGradingResultByStudentId, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getGradingResultByExamIdAndStudentId", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Services :: Grading Student Exam", description = "Grade a student's exam based on their submission and the problem description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/teacher/result/grade-student")
    public ResponseEntity<Map<String, Object>> gradingStudent(@RequestBody GradingDetailsResponse gradingDetails) {
        try {
            logger.debug("Start - gradingStudent API :: {}", gradingDetails);
            Map<String, Object> gradingStudentResponse = resultService.gradingStudent(gradingDetails);
            logger.debug("End - gradingStudent API :: {}", gradingStudentResponse);
            return new ResponseEntity<>(gradingStudentResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "gradingStudentResponse", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    //================================================================================================
    //Student Results Service Sections
    //================================================================================================
    @Operation(summary = "Student Services :: Get Result By Student ID", description = "Get Classes Result by Class ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/student/results/{studentId}")
    public ResponseEntity<Map<String, Object>> getAllExamResultsByStudentId(@PathVariable String studentId) {
        try {
            logger.debug("Start - getAllEx API :: {}", studentId);
            Map<String, Object> getAllClassesByStudentIdResponse = resultService.getResultsByStudentId(studentId);
            logger.debug("Final API Response :: {}", getAllClassesByStudentIdResponse);
            return new ResponseEntity<>(getAllClassesByStudentIdResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getClassesResultByStudentId", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Services :: Get Result By Student ID", description = "Get Classes Result by Class ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/student/result/grading-result")
    public ResponseEntity<Map<String, Object>> getGradingDetailByStudentIdAndExamId(@RequestParam String studentId, @RequestParam long examId) {
        try {
            logger.debug("Start - getGradingDetailByStudentIdAndExamId API :: studentId :: {}, examId :: {}", studentId, examId);
            Map<String, Object> getAllClassesByStudentIdResponse = resultService.getGradingResultByExamIdAndStudentId(examId, studentId);
            logger.debug("Final API Response :: {}", getAllClassesByStudentIdResponse);
            return new ResponseEntity<>(getAllClassesByStudentIdResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getGradingDetailByStudentIdAndExamId", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }



}
