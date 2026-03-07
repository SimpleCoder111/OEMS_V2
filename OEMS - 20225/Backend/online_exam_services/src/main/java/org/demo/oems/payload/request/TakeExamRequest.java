package org.demo.oems.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TakeExamRequest {

    private String studentId;

    private long examId;

    private Boolean isDemo;

    private String ipAddress;

}
