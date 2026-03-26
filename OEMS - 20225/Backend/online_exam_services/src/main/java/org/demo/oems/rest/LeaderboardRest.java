package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.service.DashboardService;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LeaderboardRest {

    private final DashboardService dashboardService;

    private final static Logger logger = LogManager.getLogger(LeaderboardRest.class);

    @Operation(summary = "Student Dashboard Service - Get Student Enrolled Subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/student/dashboard/leaderboard/exam/{examId}")
    public ResponseEntity<Map<String, Object>> studentGetRankingByExamId(@PathVariable long examId, @RequestParam String studentId){
        try{
            logger.debug("Start - studentGetRankingByExamId API :: {} :: {}", examId, studentId);
            Map<String, Object> getStudentProfileResponse = dashboardService.getStudentRankingByExamId(examId, studentId);
            logger.debug("End - studentGetRankingByExamId API :: {} :: {} :: {}", examId, studentId, getStudentProfileResponse);
            return new ResponseEntity<>(getStudentProfileResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - studentGetRankingByExamId API :: {} :: {} :: {}", examId, studentId, e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Dashboard Service - Get Student Enrolled Subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/student/dashboard/leaderboard/overall/subject/{subjectId}")
    public ResponseEntity<Map<String, Object>> studentGetRankingBySubjectId(@PathVariable long subjectId, @RequestParam String studentId){
        try{
            logger.debug("Start - studentGetRankingBySubjectId API :: {} :: {}", subjectId, studentId);
            Map<String, Object> getStudentProfileResponse = dashboardService.getStudentRankingBySubjectId(subjectId, studentId);
            logger.debug("End - studentGetRankingBySubjectId API :: {} :: {} :: {}", subjectId, studentId, getStudentProfileResponse);
            return new ResponseEntity<>(getStudentProfileResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - studentGetRankingBySubjectId API :: {} :: {} :: {}", subjectId, studentId, e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Dashboard Service - Get Student Enrolled Subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/student/dashboard/leaderboard/class/{classId}/subject/{subjectId}")
    public ResponseEntity<Map<String, Object>> studentGetRankingBySubjectId(@PathVariable long classId, @PathVariable long subjectId, @RequestParam String studentId){
        try{
            logger.debug("Start - studentGetRankingBySubjectId API :: {} :: {}", subjectId, studentId);
            Map<String, Object> getStudentProfileResponse = dashboardService.getStudentRankingBySubjectInClass(subjectId, classId, studentId);
            logger.debug("End - studentGetRankingBySubjectId API :: {} :: {} :: {}", subjectId, studentId, getStudentProfileResponse);
            return new ResponseEntity<>(getStudentProfileResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception - studentGetRankingBySubjectId API :: {} :: {} :: {}", subjectId, studentId, e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

}
