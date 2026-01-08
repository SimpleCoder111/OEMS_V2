package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "classroom")
public class ClassroomDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long clasroomId;

    @Column(name = "class_id")
    private long classId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "status")
    private String status;

}
