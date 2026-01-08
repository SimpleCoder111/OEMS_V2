package org.demo.oems.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "role_info")
@Data
public class RoleDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "role_name")
    private String roleName;
}
