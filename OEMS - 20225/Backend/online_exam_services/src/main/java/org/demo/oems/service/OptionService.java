package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.OptionBankDomain;
import org.demo.oems.repository.OptionBankRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OptionService {

    private final OptionBankRepo optionBankRepo;

    private static final Logger logger = LogManager.getLogger(OptionService.class);

    public OptionService(OptionBankRepo optionBankRepo) {
        this.optionBankRepo = optionBankRepo;
    }

    public List<OptionBankDomain> getOptionListsByQuestionId(long questionId){
        return optionBankRepo.getOptionBankDomainsByQuestionId(questionId);
    }

    public void updateOptionBank(OptionBankDomain newOptionBank){
        try {
            Optional<OptionBankDomain> currentOptionBankOpt = optionBankRepo.findById(newOptionBank.getId());
            if (currentOptionBankOpt.isPresent()) {
                logger.debug("Record Exists Going to Update");
                OptionBankDomain currentOptionBank = currentOptionBankOpt.get();
                currentOptionBank.setIsCorrect(newOptionBank.getIsCorrect());
                currentOptionBank.setQuestionId(newOptionBank.getQuestionId());
                currentOptionBank.setOptionText(newOptionBank.getOptionText());
                optionBankRepo.save(currentOptionBank);
                logger.debug("Successfully Update Records");
            }
        }catch (Exception e){
            logger.error("Exception Update Option Bank :: {}", e.getMessage());
        }
    }

    public void deleteOptionBank(long optionId){
        try{
            logger.debug("Delete Option Banks By ID :: {}", optionId);
            optionBankRepo.deleteById(optionId);

        }catch (Exception e){
            logger.error("Exception Update Option Bank :: {}", e.getMessage());
        }
    }


}
