package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentExamRankingResponse {

    private long examId;

    private String examName;

    private int rank;
}
