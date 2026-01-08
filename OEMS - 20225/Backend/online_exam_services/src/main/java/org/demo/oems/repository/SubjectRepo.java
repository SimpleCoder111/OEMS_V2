package org.demo.oems.repository;

import org.demo.oems.domain.SubjectDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepo extends JpaRepository<SubjectDomain, Long> {


}
