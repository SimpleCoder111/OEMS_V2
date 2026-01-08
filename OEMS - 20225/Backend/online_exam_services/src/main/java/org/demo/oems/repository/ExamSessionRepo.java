package org.demo.oems.repository;

import org.demo.oems.domain.ExamSessionDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamSessionRepo extends JpaRepository<ExamSessionDomain, Long> {

}
