package org.demo.oems.repository;

import org.demo.oems.domain.ClassDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClassRepo extends JpaRepository<ClassDomain, Long> {

    List<ClassDomain> getClassDomainsByTeacherIdEqualsIgnoreCase(String teacherId);

    long countByClassStartLessThanEqualAndClassEndGreaterThanEqual(
            LocalDateTime now1,
            LocalDateTime now2
    );

    long countByClassEndLessThan(LocalDateTime now);

    Optional<ClassDomain> getClassDomainByClassToken(String classToken);


}
