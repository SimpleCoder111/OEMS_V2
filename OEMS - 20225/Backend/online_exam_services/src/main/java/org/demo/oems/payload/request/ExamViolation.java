package org.demo.oems.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamViolation {
    private String studentId;

    private Long examId;

    private String violationType; // Switch Tab, Copy-Paste, etc.

    private int violationCount;

    private long examSessionId;
}
