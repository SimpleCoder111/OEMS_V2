package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.response.DashboardStatisticResponse;
import org.demo.oems.payload.response.GradeDistributionResponse;
import org.demo.oems.payload.response.RecentActivitiesResponse;
import org.demo.oems.repository.ExamRepo;
import org.demo.oems.repository.SubjectRepo;
import org.demo.oems.repository.UserInfoRepo;
import org.demo.oems.service.DashboardService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.demo.oems.utils.CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DashboardRest {
    private static final Logger logger = LogManager.getLogger(DashboardRest.class);

    private final DashboardService dashboardService;


    @GetMapping("/admin/dashboard/stats")
    @Operation(summary = "Admin Dashboard Service - Get Dashboard Stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public DashboardStatisticResponse getDashboardStatistics(){
        DashboardStatisticResponse dashboardStatisticResponse = new DashboardStatisticResponse();
        try {
            logger.debug("Trying to Get Dashboard Statistics");
            dashboardStatisticResponse = dashboardService.getDashboardStatistic();

        }catch (Exception e){
            logger.error(LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Get Dashboard Statistic", e.getMessage());
        }
        return dashboardStatisticResponse;
    }

    @Operation(summary = "Admin Dashboard Service - Get Recent Activities Default last 10 records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/admin/dashboard/activities")
    public ResponseEntity<Map<String, Object>> getRecentActivities(@RequestParam Integer limit){
        Map<String, Object> apiResponse = new HashMap<>();
        try {
            logger.debug("Trying to Get Dashboard Recent User Activities");
            apiResponse = dashboardService.getDashboardRecentActivities(limit);

            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Recent User Activities", e.getMessage());
            apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Dashboard Service - Get Overall Grade Distribution for all subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/admin/dashboard/grades")
    public List<GradeDistributionResponse> getGradeDistribution(){
        List<GradeDistributionResponse> gradeDistributionResponses = new ArrayList<>();
        try {
            logger.debug("Trying to Get Dashboard Grade Distribution");
            gradeDistributionResponses = dashboardService.getOverallGradeDistribution();
        }catch (Exception e){
            logger.error(LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Recent User Activities", e.getMessage());
        }
        return gradeDistributionResponses;
    }

    @Operation(summary = "Student Dashboard Service - Get Student Profile Information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/student/dashboard/profile")
    public ResponseEntity<Map<String, Object>> getStudentProfile(@RequestParam String studentId){
        try{
            logger.debug("Start - getStudentProfile API");
            Map<String, Object> getStudentProfileResponse = dashboardService.getUserProfile(studentId);
            logger.debug("Final API Response :: {}", getStudentProfileResponse);
            return new ResponseEntity<>(getStudentProfileResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getStudentProfile", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Dashboard Service - Get Student Enrolled Subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/student/dashboard/enrolled-subjects")
    public ResponseEntity<Map<String, Object>> getStudentEnrolledSubject(@RequestParam String studentId){
        try{
            logger.debug("Start - getStudentEnrolledSubject API");
            Map<String, Object> getStudentProfileResponse = dashboardService.getStudentEnrolledSubject(studentId);
            logger.debug("Final API Response :: {}", getStudentProfileResponse);
            return new ResponseEntity<>(getStudentProfileResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getStudentEnrolledSubject", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }
}
