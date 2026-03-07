package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetExamListsResponse {

    private Long examId;

    private Long classId;

    private Long subjectId;

    private Long examPaperId;

    private String examTitle;

    private String examDate;

    private Integer duration; //in minute

    private String examPaperType;

    private Integer easyQuestions;

    private Integer mediumQuestions;

    private Integer hardQuestions;

    private int[] questionIds;

    private String examPaperStatus;

    private String examStatus;
}
