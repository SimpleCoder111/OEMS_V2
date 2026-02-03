package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassSummaryResponse {

    private long totalClasses;

    private long onGoing;

    private long completed;

    private long totalEnrollment;

}
