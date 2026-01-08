package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_info")
@Data
public class ExamDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "exam_date")
    private LocalDateTime examDate;

    @Column(name = "exam_title")
    private String examTitle;

    @Column(name = "duration")
    private String duration;

    @Column(name = "exam_status")
    private String examStatus;
}
