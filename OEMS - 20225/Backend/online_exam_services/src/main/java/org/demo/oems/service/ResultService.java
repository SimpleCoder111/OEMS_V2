package org.demo.oems.service;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ExamDomain;
import org.demo.oems.domain.ExamResultDomain;
import org.demo.oems.repository.ExamResultRepo;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class ResultService {

    private static final Logger logger = LogManager.getLogger(ResultService.class);

    private final ExamResultRepo examResultRepo;

    private final ExamService examService;

    public ExamResultDomain findExamResultById(long resultId){
        Optional<ExamResultDomain> examResultRepoOptional = examResultRepo.findById(resultId);
        return examResultRepoOptional.orElse(null);
    }

    public List<ExamResultDomain> findAllExamResultsByStudentId(String studentId){
        List<ExamResultDomain> resultLists = examResultRepo.findExamResultDomainByStudentId(studentId);
        if(resultLists.isEmpty()) return Collections.emptyList();
        else return resultLists;
    }

    public List<ExamResultDomain> findAllExamResultByExamId(long examId){
        List<ExamResultDomain> resultLists = examResultRepo.findExamResultDomainByExam_Id(examId);
        if(resultLists.isEmpty()) return Collections.emptyList();
        else return resultLists;
    }

    public List<ExamResultDomain> findAllExamResultLists(){
        List<ExamResultDomain> resultLists = examResultRepo.findAll();
        if(resultLists.isEmpty()) return Collections.emptyList();
        else return resultLists;
    }


    public Map<String, Object> getAllClassesResult() {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Start - getAllClassesResult");
            List<ExamResultDomain> allResultLists =  findAllExamResultLists();

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", allResultLists);
            logger.debug("End - getAllClassesResult :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception - getAllClassesResult :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getClassesResultByClassId(long classId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();

        try{
            logger.debug("Start - getClassesResultByClassId :: {}", classId);
            List<ExamDomain> completedExamLists = examService.findAllCompletedExamLists(classId);

            List<ExamResultDomain> resultsLists = new ArrayList<>();

            for(ExamDomain examDomain : completedExamLists){
                long examId = examDomain.getId();

                List<ExamResultDomain> resultDomain = findAllExamResultByExamId(examId);
                if(resultDomain != null) resultsLists.addAll(resultDomain);
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", resultsLists);

            logger.debug("End - getClassesResultByClassId :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception - getClassesResultByClassId :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getResultsByStudentId(String studentId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Start - getResultsByStudentId :: {}", studentId);

            List<ExamResultDomain> resultsLists = findAllExamResultsByStudentId(studentId);

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", resultsLists);
            logger.debug("End - getResultsByStudentId :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getClassesResultByExamId(long examId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Start - getClassesResultByExamId :: {}", examId);

            List<ExamResultDomain> resultsLists = findAllExamResultByExamId(examId);

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", resultsLists);
            logger.debug("End - getClassesResultByExamId :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception - getClassesResultByExamId :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }
}
