package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardSummary {

    private int enrolledSubjects;
    private int upcomingExams;
    private int averageScore;
    private int classRank;

}
