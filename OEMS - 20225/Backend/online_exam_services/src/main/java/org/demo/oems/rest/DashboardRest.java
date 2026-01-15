package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.response.DashboardStatisticResponse;
import org.demo.oems.payload.response.GradeDistributionResponse;
import org.demo.oems.payload.response.RecentActivitiesResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardRest {
    private static final Logger logger = LogManager.getLogger(DashboardRest.class);

    /*
    Purpose: 1. Get overview statistics for the dashboard
    */

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStatisticResponse getDashboardStatistics(){
        logger.debug("Trying to Get Dashboard Statistics");
        DashboardStatisticResponse dashboardStatisticResponse = new DashboardStatisticResponse();

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
