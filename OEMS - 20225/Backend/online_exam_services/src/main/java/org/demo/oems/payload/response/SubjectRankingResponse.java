package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectRankingResponse {

    private String studentId;

    private Integer totalScore;

    private Integer rank;

    private Integer totalParticipants;
}
