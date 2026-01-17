package org.demo.oems.repository;

import org.demo.oems.domain.UserInfoDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserInfoRepo extends JpaRepository<UserInfoDomain, Long> {
    List<UserInfoDomain> findUserInfoDomainsByRoleId(Long roleId);

    Optional<UserInfoDomain> findUserInfoDomainByUserId(String userId);

    Long countByRoleId(Long roleId);

    Long countByRoleNameIgnoreCase(String roleName);

    Long countByRole_RoleNameIgnoreCaseAndCreatedAtBetween(String role, LocalDateTime start, LocalDateTime end);

    Long countByRoleIdAndCreatedAtBetween(Long roleId, LocalDateTime start, LocalDateTime end);

    List<UserInfoDomain> findByRole_RoleName(String roleName);

    Long countByRole_RoleNameIgnoreCase(String roleName);
}
