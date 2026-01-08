package org.demo.oems.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBankInsertRequest {

    private long subjectId;

    private long chapterId;

    private long questionId;

    private String questionType;

    private String questionContent;

    private String optionText;

    private String isCorrect;

    private int questionScore;

    private String difficulty;

    private String langCode;

    private String createdBy;

}
