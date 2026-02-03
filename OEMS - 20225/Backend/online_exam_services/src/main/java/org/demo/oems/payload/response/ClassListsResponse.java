package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassListsResponse {

    private long classId;

    private String className;

    private String classStart;

    private String classEnd;

    private String classStatus;

    private String classYear;

    private String teacherId;

    private String teacherName;

    private long studentCount;

}
