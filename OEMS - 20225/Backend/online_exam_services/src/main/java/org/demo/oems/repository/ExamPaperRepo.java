package org.demo.oems.repository;

import org.demo.oems.domain.ExamPaperDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamPaperRepo extends JpaRepository<ExamPaperDomain, Long> {



}
