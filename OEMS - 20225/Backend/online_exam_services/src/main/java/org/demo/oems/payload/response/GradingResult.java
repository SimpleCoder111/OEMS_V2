package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class GradingResult {
    private int obtainedScore;

    private int totalPossibleScore;

    private int answeredCount;

    private int totalQuestions;

    private String summaryMessage;
    // e.g. "85/100 - Excellent!"
    private List<QuestionGradeDetail> details; // optional – per question breakdown
}