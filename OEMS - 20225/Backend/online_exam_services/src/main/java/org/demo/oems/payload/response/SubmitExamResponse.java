package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmitExamResponse {
    private Long examSessionId;
    private String status;           // "submitted", "graded", "late_submitted"
    private LocalDateTime submittedAt;
    private Integer obtainedScore;   // final marks
    private Integer totalPossibleScore;
    private Integer answeredCount;
    private Integer totalQuestions;
    private String message;
    private Boolean isLate;
    private List<QuestionGradeDetail> questionGradeDetails; // optional - per question breakdown
}
