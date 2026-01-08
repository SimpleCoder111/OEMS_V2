package org.demo.oems.domain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;


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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "progress_data")
    private Map<String, Object> progressData; // Auto-save logic

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "last_save")
    private LocalDateTime lastSave = LocalDateTime.now();
}

