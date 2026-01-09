package org.demo.oems.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.demo.oems.domain.SubjectChapterDomain;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetChaptersBySubjectResponse {

    @JsonProperty("subjectId")
    private long subjectId;

    @JsonProperty("subjectName")
    private String subjectName;

    @JsonProperty("chapterLists")
    private List<SubjectChapterDomain> subjectChapterDomainList;

}
