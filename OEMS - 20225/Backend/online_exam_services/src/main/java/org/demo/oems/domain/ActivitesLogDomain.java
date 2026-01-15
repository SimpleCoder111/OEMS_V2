package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities_log")
@Data
public class ActivitesLogDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "user_name")
    private String name;

    @Lob
    @Column(name = "user_action", columnDefinition = "text")
    private String action;


    @Column(name = "timestamp")
    private LocalDateTime timestamp;


}
