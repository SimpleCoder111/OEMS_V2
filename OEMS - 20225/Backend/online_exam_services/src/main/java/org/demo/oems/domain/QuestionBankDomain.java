package org.demo.oems.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private long id;

    @Column(name = "subject_id")
    private long subjectId;

    @Column(name = "content")
    private String content;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "lang_code")
    private String langCode;

    @Column(name = "created_by")
    private int createdBy;
}
