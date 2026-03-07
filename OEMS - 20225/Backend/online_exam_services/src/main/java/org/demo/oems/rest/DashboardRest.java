package org.demo.oems.rest;

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
import java.util.List;
import java.util.Map;

import static org.demo.oems.utils.CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER;


@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardRest {
    private static final Logger logger = LogManager.getLogger(DashboardRest.class);

    private final DashboardService dashboardService;



    @GetMapping("/admin/stats")
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

    /*
    Purpose: 2. Fetch recent activities
    Query params: Optional limit (e.g., ?limit=10)
    */
    @GetMapping("/admin/activities")
//    @PreAuthorize("hasRole('ADMIN')")
    public List<RecentActivitiesResponse> getRecentActivities(@RequestParam int limit){
        List<RecentActivitiesResponse> recentActivitiesResponseLists = new ArrayList<>();

        try {
            logger.debug("Trying to Get Dashboard Recent User Activities");
            recentActivitiesResponseLists = dashboardService.getDashboardRecentActivities();

        }catch (Exception e){
            logger.error(LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Recent User Activities", e.getMessage());
        }



        return recentActivitiesResponseLists;
    }

    @GetMapping("/admin/grades")
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

    @GetMapping("/student/profile")
    public ResponseEntity<Map<String, Object>> getStudentProfile(@RequestParam String studentId){
        try{
            logger.debug("Start - getStudentProfile API");
            Map<String, Object> getStudentProfileResponse = dashboardService.getUserProfile(studentId);
            logger.debug("Final API Response :: {}", getStudentProfileResponse);
            return new ResponseEntity<>(getStudentProfileResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesResult", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/student/enrolled-subjects")
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

//
//    @GetMapping("/student/upcoming-exams")
//
//    @GetMapping("/student/recent-results")
//
//    @GetMapping("/student/leaderboard")
//
//    @GetMapping("/student/stats")

//    @GetMapping("/student/stats")
//    public ResponseEntity<Map<String, Object>> getStudentStats(
//            @RequestParam String studentId) {
//
//        try {
//            logger.debug("Start - getStudentEnrolledSubjects for studentId: {}", studentId);
//
//            Map<String, Object> response = dashboardService.getStudentStats(studentId);
//
//            logger.debug("Final API Response: {}", response);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            logger.error("Error in getStudentEnrolledSubjects: {}", e.getMessage(), e);
//            Map<String, Object> errorResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), null);
//            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }




}
