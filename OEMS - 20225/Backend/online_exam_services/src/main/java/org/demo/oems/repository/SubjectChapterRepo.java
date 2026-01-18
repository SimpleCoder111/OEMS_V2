package org.demo.oems.repository;

import org.demo.oems.domain.ChapterDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectChapterRepo  extends JpaRepository<ChapterDomain, Long>{
    List<ChapterDomain> findSubjectChapterDomainsBySubjectId(Long subjectId);

    Optional<ChapterDomain> findSubjectChapterDomainById(Long chapterId);

    Optional<ChapterDomain> findByChapterEqualsIgnoreCaseAndSubjectId(String chapterName, Long subjectId);
}
