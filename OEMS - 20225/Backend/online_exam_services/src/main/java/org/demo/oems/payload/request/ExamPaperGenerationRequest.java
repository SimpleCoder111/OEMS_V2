package org.demo.oems.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamPaperGenerationRequest {
    private int easyQuestion;

    private int mediumQuestion;

    private int hardQuestions;

    private int desiredEasyScore;

    private int desiredMediumScore;

    private int desiredHardScore;

    private long subjectId;
}
