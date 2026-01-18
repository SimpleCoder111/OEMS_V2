package org.demo.oems.repository;

import org.demo.oems.domain.ClassDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepo extends JpaRepository<ClassDomain, Long> {

    List<ClassDomain> getClassDomainsByTeacherIdEqualsIgnoreCase(String teacherId);

}
