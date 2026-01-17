package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_id_sequence")
@Data
@IdClass(UserIdSequenceId.class)
public class UserIDSequenceDomain {

    @Id
    @Column(name = "role_code")
    private String roleCode;

    @Id
    @Column(name = "year")
    private Integer year;

    @Column(name = "current_value")
    private Long currentValue;

    // getters & setters
}
