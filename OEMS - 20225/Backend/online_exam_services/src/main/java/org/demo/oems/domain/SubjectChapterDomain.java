package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "subject_chapter")
@Data
@RequiredArgsConstructor
public class SubjectChapterDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "chapter", nullable = false, length = 100)
    private String chapter;

}
