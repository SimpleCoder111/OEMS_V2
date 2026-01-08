package org.demo.oems.domain;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal score;

    @Column(name = "time_taken", nullable = false)
    private Integer timeTaken; // Seconds, for tie-breaking rankings [2]

    @Column(name = "graded_at")
    private LocalDateTime gradedAt = LocalDateTime.now();
}
