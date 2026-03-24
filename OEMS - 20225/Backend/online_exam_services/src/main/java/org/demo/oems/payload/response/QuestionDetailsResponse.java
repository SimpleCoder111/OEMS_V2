package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDetailsResponse {
    private long id;

    private long chapterId;

    private String chapterName;

    private int chapterOrder;

    private String questionType;

    private String questionContent;

    private List<String> optionContent;

    private String correctAnswer;

    private String difficulty;

    private String createdBy;

    private String points;

    private String createdAt;
}
