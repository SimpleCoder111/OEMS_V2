package org.demo.oems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.demo.oems.domain.RoleDomain;

public interface RoleRepo extends JpaRepository<RoleDomain, Long> {

}
