package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "class_info")
@Data
public class ClassDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long classId;

    @Column(name = "class_name")
    private String className;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "class_start")
    private LocalDateTime classStart;

    @Column(name = "class_end")
    private LocalDateTime classEnd;

    @Column(name = "class_status")
    private String classStatus;

    @Column(name = "academic_year")
    private String academicYear;


}
