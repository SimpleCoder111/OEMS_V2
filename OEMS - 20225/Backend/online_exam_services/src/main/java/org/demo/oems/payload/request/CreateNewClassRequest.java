package org.demo.oems.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewClassRequest {

    private String className;

    private String classStart;

    private String classEnd;

    private Long subjectId;

    private String classStatus;

    private String teacherId;

    private String academicYear;

}
