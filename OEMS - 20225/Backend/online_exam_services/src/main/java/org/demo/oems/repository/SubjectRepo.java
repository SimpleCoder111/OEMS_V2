package org.demo.oems.repository;

import org.demo.oems.domain.SubjectDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SubjectRepo extends JpaRepository<SubjectDomain, Long> {
    Optional<SubjectDomain> getSubjectDomainsById(Long id);

    long countByStatus(String subjectStatus);

    long countByCreatedAtBefore(LocalDateTime date);

}
