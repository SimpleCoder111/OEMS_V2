package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "classroom")
public class ClassroomDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long enrollmentId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "status", length = 20)
    private String status = "active";  // active, inactive, graduated, etc.

    // Optional: enrollment date
    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt = LocalDateTime.now();

}
