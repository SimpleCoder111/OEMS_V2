package org.demo.oems.repository;

import org.demo.oems.domain.UserInfoDomain;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepo extends JpaRepository<UserInfoDomain, Long> {
    List<UserInfoDomain> findUserInfoDomainsByRoleId(int roleId);

    Optional<UserInfoDomain> findUserInfoDomainByUserId(String userId);
}
