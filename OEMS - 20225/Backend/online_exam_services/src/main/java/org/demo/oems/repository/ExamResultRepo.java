package org.demo.oems.repository;

import org.demo.oems.domain.ExamResultDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamResultRepo extends JpaRepository<ExamResultDomain, Long> {

}
