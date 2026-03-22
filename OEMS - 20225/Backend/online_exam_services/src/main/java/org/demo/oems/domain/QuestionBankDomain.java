package org.demo.oems.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    // Relationship to Subject (instead of raw subjectId)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectDomain subject;

    // Relationship to Chapter (instead of raw chapterId)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private ChapterDomain chapter;

    @Column(name = "question_type", nullable = false)
    private String questionType;

    @Column(name = "question_content", columnDefinition = "TEXT")
    private String questionContent;

    @Column(name = "option_content", columnDefinition = "TEXT", nullable = true)
    private String optionContent;

    @Column(name = "correctAnswer", columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "points")
    private int points;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}