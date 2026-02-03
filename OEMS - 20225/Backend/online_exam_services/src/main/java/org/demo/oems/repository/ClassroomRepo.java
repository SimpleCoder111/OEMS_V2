package org.demo.oems.repository;

import org.demo.oems.domain.ClassroomDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepo extends JpaRepository<ClassroomDomain, Long> {

    long countByClassId(Long classId);

}
