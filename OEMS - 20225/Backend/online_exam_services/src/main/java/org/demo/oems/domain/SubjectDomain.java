package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subject_info")
@Data
@RequiredArgsConstructor
public class SubjectDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "subject_code", length = 100)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;

    @Column(name = "subject_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "subject_status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
