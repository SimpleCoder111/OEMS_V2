package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "class_group")
@Data
public class ClassGroupDomain {

    // New Entity: ClassGroupDomain - Represents the group (e.g., "9A", "10B")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long groupId;

    @Column(name = "group_name", nullable = false)  // e.g., "9A", "10B"
    private String groupName;

    @Column(name = "academic_year", length = 20)
    private String academicYear;  // e.g., "2025-2026"

    // Optional: homeroom teacher, description, etc.
    @Column(name = "homeroom_teacher_id")
    private String homeroomTeacherId;

    // One group has many subject classes
    @OneToMany(mappedBy = "classGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassDomain> subjectClasses;

    // One group has many student enrollments
    @OneToMany(mappedBy = "classGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassroomDomain> enrollments;

}
