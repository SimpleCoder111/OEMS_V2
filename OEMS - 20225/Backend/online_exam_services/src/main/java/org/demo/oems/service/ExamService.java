package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.OptionBankDomain;
import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.domain.ChapterDomain;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.ExamPaperGenerationRequest;
import org.demo.oems.payload.response.OptionListResponse;
import org.demo.oems.payload.response.QuestionBankListsResponse;
import org.demo.oems.repository.QuestionBankRepo;
import org.demo.oems.repository.SubjectChapterRepo;
import org.demo.oems.repository.SubjectRepo;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    private final QuestionBankRepo questionBankRepo;


    private final OptionService optionService;

    private final SubjectRepo subjectRepo;

    private final SubjectChapterRepo chapterRepo;


    public ExamService(QuestionBankRepo questionBankRepo, OptionService optionService, SubjectRepo subjectRepo, SubjectChapterRepo chapterRepo) {
        this.questionBankRepo = questionBankRepo;
        this.optionService = optionService;
        this.subjectRepo = subjectRepo;
        this.chapterRepo = chapterRepo;
    }

    private static final Logger logger = LogManager.getLogger(ExamService.class);

    public JSONObject getExamPaper(ExamPaperGenerationRequest request){
        logger.debug("Generate Exam Paper Services with request :: {}", request);
        JSONObject finalResponse = new JSONObject();
        List<QuestionBankDomain> combinedLists = new ArrayList<>();
        try{
            List<QuestionBankDomain> easyQuestionLists = questionBankRepo.findRandomNativeByDifficultyAndSubjectId("EASY", request.getEasyQuestion(), request.getSubjectId());
            List<QuestionBankDomain> mediumQuestionLists = questionBankRepo.findRandomNativeByDifficultyAndSubjectId("MEDIUM", request.getMediumQuestion(), request.getSubjectId());
            List<QuestionBankDomain> hardQuestionLists = questionBankRepo.findRandomNativeByDifficultyAndSubjectId("HARD", request.getHardQuestions(), request.getSubjectId());
            combinedLists.addAll(easyQuestionLists);
            combinedLists.addAll(mediumQuestionLists);
            combinedLists.addAll(hardQuestionLists);

            Optional<SubjectDomain> subjectDomainOptional = subjectRepo.getSubjectDomainsById(request.getSubjectId());
            SubjectDomain subjectInfo = new SubjectDomain();

            if(subjectDomainOptional.isPresent()) {
                subjectInfo = subjectDomainOptional.get();
            }

            List<QuestionBankListsResponse> questionListsResponse = new ArrayList<>();
            for (QuestionBankDomain questionBank : combinedLists) {
                QuestionBankListsResponse questionResponse = new QuestionBankListsResponse();

                questionResponse.setQuestionType(String.valueOf(questionBank.getQuestionType()));
                questionResponse.setQuestionId(questionBank.getId());
                questionResponse.setQuestionContent(questionBank.getQuestionContent());
                questionResponse.setDifficulty(String.valueOf(questionBank.getDifficulty()));
                questionResponse.setCreatedBy(questionBank.getCreatedBy());

                Optional<ChapterDomain> chapterDomainOptional = chapterRepo.findSubjectChapterDomainById(questionBank.getChapter().getId());
                if(chapterDomainOptional.isPresent()){
                    ChapterDomain chapterDomain = chapterDomainOptional.get();
                    questionResponse.setChapterId(chapterDomain.getId());
                    questionResponse.setChapter(chapterDomain.getChapter());
                }

                List<OptionBankDomain> optionLists = optionService.getOptionListsByQuestionId(questionBank.getId());

                List<OptionListResponse> optionResponseLists = QuestionBankService.getOptionListResponses(optionLists);
                questionResponse.setOptionLists(optionResponseLists);

                questionListsResponse.add(questionResponse);
            }


            finalResponse = ResponseUtils.formatServiceResponse("0", "success");
            finalResponse.put("questionData", questionListsResponse);
            finalResponse.put("subjectId", subjectInfo.getId());
            finalResponse.put("subjectName", subjectInfo.getSubjectName());
        }catch (Exception e){
            logger.error("Exception while Generate Exam Paper :: {}" , e.getMessage());
            finalResponse = ResponseUtils.formatServiceResponse("1", e.getMessage());
        }
        return finalResponse;
    }
}
