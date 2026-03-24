package org.demo.oems.domain;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "exam_result")
@Data
public class ExamResultDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private ExamDomain exam;

    @Column(name = "student_id")
    private String studentId;

    @Column(name ="score", nullable = false, precision = 5, scale = 2)
    private Integer score;

    @Column(name = "status", nullable = false)
    private String status; // e.g., "graded", "pending", "needs review"

    @Column(name = "time_taken", nullable = false)
    private long timeTaken; // Seconds, for tie-breaking rankings [2]

    @Column(name = "graded_at")
    private LocalDateTime gradedAt = LocalDateTime.now();

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "grade")
    private String grade; // e.g., "A", "B", "C", "D", "F"
}
