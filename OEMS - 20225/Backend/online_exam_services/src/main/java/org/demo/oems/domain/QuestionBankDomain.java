// Updated QuestionBankDomain - Proper JPA relationships + enums
package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionBankDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    // Relationship to Subject (instead of raw subjectId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectDomain subject;

    // Relationship to Chapter (instead of raw chapterId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private ChapterDomain chapter;

    // Use enum with @Enumerated
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    // Fixed typo: quesiton_content → question_content
    @Column(name = "question_content", columnDefinition = "TEXT")
    private String questionContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Enums (move outside class or keep inner – outer is cleaner)
    public enum QuestionType {
        MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}