package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamResultsResponse {
    private Long id;

    private String examId;

    private String examName;

    private String classId;

    private String studentId;

    private long subjectId;

    private String subjectName;

    private Integer score;

    private String status; // e.g., "graded", "pending", "needs review"

    private long timeTaken; // Seconds, for tie-breaking rankings [2]

    private LocalDateTime gradedAt = LocalDateTime.now();

    private String grade;

    private String studentName;

}
