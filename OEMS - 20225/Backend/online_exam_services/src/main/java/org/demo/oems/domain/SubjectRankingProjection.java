package org.demo.oems.domain;

public interface SubjectRankingProjection {
    String getStudentId();
    Integer getTotalScore();
    Long getTotalTimeTaken();
    Integer getFinalRank();
}
