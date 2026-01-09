package org.demo.oems.repository;

import org.demo.oems.domain.SubjectChapterDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectChapterRepo  extends JpaRepository<SubjectChapterDomain, Long>{
    List<SubjectChapterDomain> findSubjectChapterDomainsBySubjectId(Long subjectId);

    Optional<SubjectChapterDomain> findSubjectChapterDomainById(long chapterId);

}
