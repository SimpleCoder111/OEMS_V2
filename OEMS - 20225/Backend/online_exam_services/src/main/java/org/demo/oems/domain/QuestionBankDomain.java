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
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "chapter_id")
    private Long chapterId;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "quesiton_content")
    private String questionContent;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "created_by")
    private String createdBy;
}
