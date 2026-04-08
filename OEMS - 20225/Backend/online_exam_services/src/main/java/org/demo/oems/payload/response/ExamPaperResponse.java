package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamPaperResponse {

    private String studentId;

    private String studentName;

    private Long subjectId;

    private String subjectName;

    private Long classId;

    private String className;

    private Long examId;

    private String examTitle;

    private int examDuration;

    private Long examSessionId;

    private List<ExamPaperQuestionResponse> questionLists;

    private String ipAddress;

    private String latency;

}
