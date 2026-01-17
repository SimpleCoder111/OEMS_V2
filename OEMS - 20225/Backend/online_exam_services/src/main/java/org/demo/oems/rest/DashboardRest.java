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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.demo.oems.utils.CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER;


@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardRest {
    private static final Logger logger = LogManager.getLogger(DashboardRest.class);

    private final DashboardService dashboardService;


    /*
    Purpose: 1. Get overview statistics for the dashboard
    */

    @GetMapping("/stats")
//    @PreAuthorize("hasRole('ADMIN')")
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
    @GetMapping("/activities")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RecentActivitiesResponse> getRecentActivities(@RequestParam int limit){
        List<RecentActivitiesResponse> recentActivitiesResponseLists = new ArrayList<>();

        return recentActivitiesResponseLists;
    }

    @GetMapping("/grades")
    @PreAuthorize("hasRole('ADMIN')")
    public List<GradeDistributionResponse> getGradeDistribution(){
        List<GradeDistributionResponse> gradeDistributionResponses = new ArrayList<>();

        return gradeDistributionResponses;
    }
}
