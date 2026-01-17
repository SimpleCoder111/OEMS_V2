package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "option_bank")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class OptionBankDomain {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "option_text")
    private String optionText;

    @Column(name = "is_correct")
    private Boolean isCorrect;

}
