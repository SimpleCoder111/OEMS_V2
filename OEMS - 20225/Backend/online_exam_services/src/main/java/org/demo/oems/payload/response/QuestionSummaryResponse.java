package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSummaryResponse {

    private long totalQuestions;

    private long totalEasyQuestions;

    private long totalHardQuestions;

    private long totalMediumQuestions;

    private long totalMCQQuestions;

    private long totalFillBlankQuestions;

    private long totalTrueFalseQuestions;

}
