package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "exam_paper")
@Data
public class ExamPaperDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "exam_paper_type") //Manual or Auto
    private String examPaperType;

    @Column(name = "easy_question") //Auto
    private int easyQuestions;

    @Column(name = "medium_question") //Auto
    private int mediumQuestions;

    @Column(name = "hard_question") //Auto
    private int hardQuestions;

    @Column(name = "question_id_array") //Manual
    private String questionIdArrayString;

    @Column(name = "exam_paper_status") //Draft, Publish
    private String examPaperStatus;
}
