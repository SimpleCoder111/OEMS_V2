package org.demo.oems.repository;

import org.demo.oems.domain.ClassDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepo extends JpaRepository<ClassDomain, Long> {

}
