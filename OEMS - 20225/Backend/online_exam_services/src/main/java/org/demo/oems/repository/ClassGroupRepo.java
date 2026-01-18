package org.demo.oems.repository;

import org.demo.oems.domain.ClassGroupDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassGroupRepo extends JpaRepository<ClassGroupDomain, Long> {
}
