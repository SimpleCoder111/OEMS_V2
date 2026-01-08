package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "question_bank")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class QuestionBankDomain {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "subject_id")
    private long subjectId;

    @Column(name = "chapter_id")
    private long chapterId;

    @Column(name = "question_id")
    private long questionId;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "quesiton_content")
    private String questionContent;

    @Column(name = "option_text")
    private String optionText;

    @Column(name = "is_correct")
    private String isCorrect;

    @Column(name = "question_score")
    private int questionScore;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "lang_code")
    private String langCode;

    @Column(name = "created_by")
    private String createdBy;
}
