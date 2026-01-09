package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject_info")
@Data
@RequiredArgsConstructor
public class SubjectDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;

    @Column(name = "subject_description", columnDefinition = "TEXT")
    private String description;
}
