package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatisticResponse {

    private int totalStudent;

    private int totalTeacher;

    private int activeSubject;

    private int examThisMonth;

    private String studentChange; //ex: +1%

    private String teacherChange;

    private String subjectChange;

    private String examChange;

}
