package org.demo.oems.service;

import org.demo.oems.repository.ExamRepo;
import org.springframework.stereotype.Service;

@Service
public class ExamService {
    private final ExamRepo examRepo;

    public ExamService(ExamRepo examRepo) {
        this.examRepo = examRepo;
    }

    public void getExamPaper(){

    }
}
