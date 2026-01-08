package org.demo.oems.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "user_id", length = 10, nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, length = 50)
    private String name; // Supports Khmer/Chinese Unicode

    @Column(nullable = false, length = 256)
    private String password;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 2)
    private String gender;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(length = 50)
    private String email;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(length = 256)
    private String address;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
