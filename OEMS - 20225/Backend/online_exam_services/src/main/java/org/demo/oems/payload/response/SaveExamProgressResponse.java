package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveExamProgressResponse {
    private Long examSessionId;

    private String status;

    private LocalDateTime lastSaved;

    private String message;

    private Integer answeredQuestionsCount;

    private Integer totalQuestionsCount;
}
