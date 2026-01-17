package org.demo.oems.repository;

import jakarta.persistence.LockModeType;
import org.demo.oems.domain.UserIDSequenceDomain;
import org.demo.oems.domain.UserIdSequenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserIDSequenceRepo extends JpaRepository<UserIDSequenceDomain, UserIdSequenceId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT s FROM UserIDSequenceDomain s
    WHERE s.roleCode = :role
    AND s.year = :year
    """)
    Optional<UserIDSequenceDomain> findForUpdate(
            @Param("role") String role,
            @Param("year") Integer year
    );

}
