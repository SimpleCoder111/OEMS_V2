package org.demo.oems.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_session")
@Data
public class ExamSessionDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserInfoDomain student;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private ExamDomain exam;

    @Column(length = 20)
    private String status = "IN_PROGRESS";

    @Column(name = "progress_data", columnDefinition = "TEXT")
    private String progressData; // Auto-save logic

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "last_save")
    private LocalDateTime lastSave = LocalDateTime.now();

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Column(name = "violation_count")
    private int violationCount = 0;

    @Column(name = "latency", length = 45)
    private String latency;
}

