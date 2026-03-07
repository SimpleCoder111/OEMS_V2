package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionGradeDetail {
    private Long questionId;

    private String questionType;

    private int pointsPossible;

    private int pointsObtained;

    private boolean isCorrect;

    private String studentAnswer;

    private String correctAnswer;
}