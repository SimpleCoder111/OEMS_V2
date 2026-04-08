package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamMonitorMessage {

    private String studentName;

    private String examTitle;

    private String studentId;

    private Long examId;

    private Long classId;

    private String status; // ACTIVE, DISCONNECTED, SUBMITTED

    private String violationType; // Switch Tab, Copy-Paste, etc.

    private int violationCount;

    private String latency;

    private String ipAddress;

    private long remainingTime;

    private String eventType; // JOIN, HEARTBEAT, VIOLATION, SUBMIT, DISCONNECT

    private LocalDateTime currentTime;

    private String message; // Optional field for additional info
}
