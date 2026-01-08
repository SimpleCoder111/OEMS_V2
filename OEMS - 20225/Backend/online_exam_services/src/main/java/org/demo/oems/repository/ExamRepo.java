package org.demo.oems.repository;

import org.demo.oems.domain.ExamDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepo extends JpaRepository<ExamDomain, Long> {

}
