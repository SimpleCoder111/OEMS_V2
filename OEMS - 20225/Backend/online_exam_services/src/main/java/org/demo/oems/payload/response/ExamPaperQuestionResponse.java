package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamPaperQuestionResponse {

    private Long questionId;

    private String questionText;

    private String questionType;

    private Long chapterId;

    private String chapterName;

    private List<String> optionLists;

    private String studentAnswer;

}
