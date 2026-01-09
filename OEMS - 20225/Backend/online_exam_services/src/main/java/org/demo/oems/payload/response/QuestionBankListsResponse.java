package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBankListsResponse {
    private long questionId;

    private long chapterId;

    private String chapter;

    private String questionType;

    private String questionContent;

    private String difficulty;

    private String createdBy;

    private List<OptionListResponse> optionLists;

}
