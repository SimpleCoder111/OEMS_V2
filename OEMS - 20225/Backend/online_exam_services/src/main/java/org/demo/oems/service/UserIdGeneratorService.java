package org.demo.oems.service;

import lombok.RequiredArgsConstructor;
import org.demo.oems.domain.UserIDSequenceDomain;
import org.demo.oems.repository.UserIDSequenceRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class UserIdGeneratorService {
    private static final int SEQ_LENGTH = 5;

    private final UserIDSequenceRepo sequenceRepo;

    @Transactional
    public String generateStudentId() {
        return generate("STUDENT", "S");
    }

    @Transactional
    public String generateTeacherId() {
        return generate("TEACHER", "T");
    }

    @Transactional
    public String generateAdminId() {
        return generate("ADMIN", "A");
    }

    private String generate(String roleCode, String prefix) {
        int year = Year.now().getValue();

        UserIDSequenceDomain seq = sequenceRepo
                .findForUpdate(roleCode, year)
                .orElseGet(() -> createNew(roleCode, year));

        long next = seq.getCurrentValue() + 1;
        seq.setCurrentValue(next);
        sequenceRepo.save(seq);

        return prefix + year + pad(next);
    }

    private UserIDSequenceDomain createNew(String roleCode, int year) {
        UserIDSequenceDomain seq = new UserIDSequenceDomain();
        seq.setRoleCode(roleCode);
        seq.setYear(year);
        seq.setCurrentValue(0L);
        return seq;
    }

    private String pad(long value) {
        return String.format("%0" + SEQ_LENGTH + "d", value);
    }

}
