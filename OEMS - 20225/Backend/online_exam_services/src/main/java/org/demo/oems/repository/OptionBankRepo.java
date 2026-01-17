package org.demo.oems.repository;

import org.demo.oems.domain.OptionBankDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionBankRepo extends JpaRepository<OptionBankDomain, Long> {

    List<OptionBankDomain> getOptionBankDomainsByQuestionId(Long questionId);

}
