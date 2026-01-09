package org.demo.oems.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBankInsertRequest {
    @JsonProperty("subjectId")
    private long subjectId;

    @JsonProperty("chapterId")
    private long chapterId;

    @JsonProperty("questionType")
    private String questionType;

    @JsonProperty("questionContent")
    private String questionContent;

    @JsonProperty("difficulty")
    private String difficulty;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("optionLists")
    List<OptionBankInsertRequest> optionLists;
}
