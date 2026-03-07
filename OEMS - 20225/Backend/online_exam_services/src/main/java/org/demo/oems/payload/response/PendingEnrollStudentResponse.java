package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingEnrollStudentResponse {

    private String studentName;

    private String studentEmail;

    private String requestAt;

    private String status;

    private Long classEnrolledId;

}
