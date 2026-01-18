package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "subject_chapter")
@Data
@RequiredArgsConstructor
public class ChapterDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Relationship back to Subject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectDomain subject;

    @Column(name = "chapter", nullable = false, length = 256)
    private String chapter;

    @Column(name = "chapter_index")
    private int chapterIndex;

    @Column(name = "active")
    private String chapterStatus = "active";
}