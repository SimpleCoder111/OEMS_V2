package org.demo.oems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.demo.oems.domain.RoleDomain;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<RoleDomain, Long> {
    Optional<RoleDomain> findRoleDomainByRoleNameEqualsIgnoreCase(String roleName);

}
