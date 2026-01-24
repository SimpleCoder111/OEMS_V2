package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectSummaryResponse {

    long totalSubject;

    long activeSubject;

    long totalChapter;

    long totalQuestion;

}
