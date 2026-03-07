package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentEnrolledSubjectResponse {

    private String subjectId;

    private String subjectName;

    private String teacherName;

    private String nextExamDate;

}
