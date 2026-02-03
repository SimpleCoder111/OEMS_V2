package org.demo.oems.payload.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateClassInfoRequest {

    private String className;

    private String subjectName;

    private long subjectId;

    private String teacherId;

    private String classStart;

    private String classEnd;

    private String classStatus;

    private String academicYear;

}
