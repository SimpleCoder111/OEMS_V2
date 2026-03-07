package org.demo.oems.payload.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@NotNull
public class ExamPaperSubmitOrSaveRequest {

    @NotNull(message = "Exam session ID is required")
    private Long examSessionId;

    @NotNull(message = "Student ID is required")
    private String studentId;

    private List<ExamPaperQuestionResponse> questionLists;

}