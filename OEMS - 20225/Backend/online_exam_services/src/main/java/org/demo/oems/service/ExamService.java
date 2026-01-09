package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.payload.request.ExamPaperGenerationRequest;
import org.demo.oems.repository.ExamRepo;
import org.demo.oems.repository.QuestionBankRepo;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamService {
    private final ExamRepo examRepo;

    private final QuestionBankRepo questionBankRepo;
    private static final Logger logger = LogManager.getLogger(QuestionBankService.class);

    public ExamService(ExamRepo examRepo, QuestionBankRepo questionBankRepo) {
        this.examRepo = examRepo;
        this.questionBankRepo = questionBankRepo;
    }

    public JSONObject getExamPaper(ExamPaperGenerationRequest request){
        logger.debug("Generate Exam Paper Services with request :: {}", request);
        JSONObject finalResponse = new JSONObject();
        List<QuestionBankDomain> combinedLists = new ArrayList<>();
        try{
            List<QuestionBankDomain> easyQuestionLists = questionBankRepo.findRandomNativeByDifficultyAndSubjectId("EASY", request.getEasyQuestion(), request.getSubjectId());
            List<QuestionBankDomain> mediumQuestionLists = questionBankRepo.findRandomNativeByDifficultyAndSubjectId("MEDIUM", request.getEasyQuestion(), request.getSubjectId());
            List<QuestionBankDomain> hardQuestionLists = questionBankRepo.findRandomNativeByDifficultyAndSubjectId("HARD", request.getEasyQuestion(), request.getSubjectId());
            combinedLists.addAll(easyQuestionLists);
            combinedLists.addAll(mediumQuestionLists);
            combinedLists.addAll(hardQuestionLists);

            finalResponse = ResponseUtils.responseFormatUtils("0", "success");
            finalResponse.put("examPaper", combinedLists);
        }catch (Exception e){
            logger.error("Exception while Generate Exam Paper :: {}" , e.getMessage());
            finalResponse = ResponseUtils.responseFormatUtils("1", e.getMessage());
        }
        return finalResponse;
    }
}
