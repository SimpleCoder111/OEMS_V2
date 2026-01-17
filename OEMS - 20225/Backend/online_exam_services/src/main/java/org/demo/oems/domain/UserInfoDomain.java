package org.demo.oems.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "user_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDomain implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id", length = 10, nullable = false, unique = true)
    private String userId;  // Login identifier (username)

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 256)
    private String password;  // BCrypt hashed

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 2)
    private String gender;

    // Relationship to RoleDomain table
    @ManyToOne(fetch = FetchType.EAGER)  // EAGER ensures role is loaded during authentication
    @JoinColumn(name = "role_id", nullable = false)
    private RoleDomain role;

    @Column(length = 50)
    private String email;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(length = 256)
    private String address;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Add this field to your existing UserInfoDomain entity
    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;  // e.g., "/uploads/profile/S2026A0001.jpg"

    // Optional: Default avatar if null
    @Transient
    public String getDisplayProfileImageUrl() {
        return profileImageUrl != null ? profileImageUrl : "/images/default-avatar.png";  // Serve a default image
    }

    // ==================== Spring Security UserDetails Methods ====================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Assumes roleName in RoleDomain is uppercase (e.g., "ADMIN", "TEACHER", "STUDENT")
        String roleName = role.getRoleName().toUpperCase();
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userId;  // userId is the unique login field
    }

    // Default account status - customize later (e.g., add enabled/locked columns)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Transient
    public String getRoleName() {
        return role != null ? role.getRoleName().toUpperCase() : "STUDENT";
    }
}