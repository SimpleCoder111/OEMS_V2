package org.demo.oems.repository;

import org.demo.oems.domain.ClassroomDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepo extends JpaRepository<ClassroomDomain, Long> {

    long countByClassId(Long classId);

    Optional<ClassroomDomain> findClassroomDomainByClassIdAndStudentId(long classId, String studentId);

    List<ClassroomDomain> findClassroomDomainsByClassIdAndStatusEqualsIgnoreCase(long classId, String status);

    List<ClassroomDomain> findClassroomDomainsByStudentIdAndStatus(String studentId, String status);

    long countDistinctSubjectByStudentIdAndStatus(String studentId, String status);

    List<ClassroomDomain> findClassroomDomainsByClassId(long classId);


}
