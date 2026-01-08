package org.demo.oems.payload.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateClassInfoRequest {

    private String className;

    private String teacherId;

    private String classStart;

    private String classEnd;

    private String classStatus;

    private String classYear;

}
