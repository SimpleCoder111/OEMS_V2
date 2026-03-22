package org.demo.oems.repository;

import org.demo.oems.domain.ActivityLogDomain;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityLogRepo extends JpaRepository<ActivityLogDomain, Long> {

    @Query("SELECT a FROM ActivityLogDomain a ORDER BY a.timestamp DESC")
    List<ActivityLogDomain> findAllOrderedByTimestampDesc();

    @Query("SELECT a FROM ActivityLogDomain a ORDER BY a.timestamp DESC")
    List<ActivityLogDomain> findAllOrderedByTimestampDescWithLimit(@Param("limit") int limit);

    @Query("SELECT a FROM ActivityLogDomain a ORDER BY a.timestamp DESC")
    List<ActivityLogDomain> findTopByOrderByTimestampDesc(@Param("limit") int limit);

    @Query("SELECT a FROM ActivityLogDomain a ORDER BY a.timestamp DESC")
    List<ActivityLogDomain> findRecentLogs(Pageable pageable);



}
