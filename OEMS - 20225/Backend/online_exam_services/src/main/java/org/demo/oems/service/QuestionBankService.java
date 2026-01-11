package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.OptionBankDomain;
import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.domain.SubjectChapterDomain;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.OptionBankInsertRequest;
import org.demo.oems.payload.request.QuestionBankInsertRequest;
import org.demo.oems.payload.response.OptionListResponse;
import org.demo.oems.payload.response.QuestionBankListsResponse;
import org.demo.oems.repository.OptionBankRepo;
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
public class QuestionBankService {
    private static final Logger logger = LogManager.getLogger(QuestionBankService.class);
    private final QuestionBankRepo questionBankRepo;
    private final OptionBankRepo optionBankRepo;

    private final SubjectRepo subjectRepo;
    private final SubjectChapterRepo chapterRepo;

    public QuestionBankService(QuestionBankRepo questionBankRepo,
                               OptionBankRepo optionBankRepo, SubjectRepo subjectRepo, SubjectChapterRepo chapterRepo) {
        this.questionBankRepo = questionBankRepo;
        this.optionBankRepo = optionBankRepo;
        this.subjectRepo = subjectRepo;
        this.chapterRepo = chapterRepo;
    }

    public JSONObject getAllQuestionBanksBySubject(long subjectId){
        logger.debug("Get All Question Banks By Subject Services Start");

        JSONObject finalResponse = new JSONObject();
        List<QuestionBankListsResponse> questionListsResponse = new ArrayList<>();

        try {
            List<QuestionBankDomain> questionBankDomainList = questionBankRepo.getQuestionBankDomainsBySubjectId(subjectId);

            Optional<SubjectDomain> subjectDomainOptional = subjectRepo.getSubjectDomainsById(subjectId);


            SubjectDomain subjectInfo = new SubjectDomain();

            if(subjectDomainOptional.isPresent()) {
                subjectInfo = subjectDomainOptional.get();
            }


            for (QuestionBankDomain questionBankDomain : questionBankDomainList) {
                QuestionBankListsResponse questionResponse = new QuestionBankListsResponse();

                questionResponse.setQuestionType(questionBankDomain.getQuestionType());
                questionResponse.setQuestionId(questionBankDomain.getId());
                questionResponse.setQuestionContent(questionBankDomain.getQuestionContent());
                questionResponse.setDifficulty(questionBankDomain.getDifficulty());
                questionResponse.setCreatedBy(questionBankDomain.getCreatedBy());

                Optional<SubjectChapterDomain> chapterDomainOptional = chapterRepo.findSubjectChapterDomainById(questionBankDomain.getChapterId());
                if(chapterDomainOptional.isPresent()){
                    SubjectChapterDomain chapterDomain = chapterDomainOptional.get();
                    questionResponse.setChapterId(chapterDomain.getId());
                    questionResponse.setChapter(chapterDomain.getChapter());
                }

                List<OptionBankDomain> optionBankLists = optionBankRepo.getOptionBankDomainsByQuestionId(questionBankDomain.getId());

                List<OptionListResponse> optionResponseLists = getOptionListResponses(optionBankLists);
                questionResponse.setOptionLists(optionResponseLists);

                questionListsResponse.add(questionResponse);


            }
            finalResponse = ResponseUtils.responseFormatUtils("0", "Success");
            finalResponse.put("questionData", questionListsResponse);
            finalResponse.put("subjectId", subjectId);
            finalResponse.put("subjectName", subjectInfo.getSubjectName());
        }catch (Exception e){
            logger.error("Exception Get All Question Banks By Subject Services :: {}", e.getMessage());
            finalResponse = ResponseUtils.responseFormatUtils("1", e.getMessage());
        }

        return finalResponse;
    }

    public static List<OptionListResponse> getOptionListResponses(List<OptionBankDomain> optionBankLists) {
        List<OptionListResponse> optionResponseLists = new ArrayList<>();


        for (OptionBankDomain optionBankDomain : optionBankLists) {

            OptionListResponse optionResponse = new OptionListResponse();

            optionResponse.setOptionText(optionBankDomain.getOptionText());
            optionResponse.setOptionId(optionBankDomain.getId());
            optionResponse.setIsCorrect(optionBankDomain.getIsCorrect());

            optionResponseLists.add(optionResponse);
        }
        return optionResponseLists;
    }


    public JSONObject addQuestionBanks(QuestionBankInsertRequest requestPayload){
        JSONObject addQuestionBankResponse = new JSONObject();
        String responseStatus;
        String responseMessage;
        try{
            QuestionBankDomain newQuestionBank = new QuestionBankDomain();
            newQuestionBank.setQuestionType(requestPayload.getQuestionType());
            newQuestionBank.setQuestionContent(requestPayload.getQuestionContent());
            newQuestionBank.setDifficulty(requestPayload.getDifficulty());
            newQuestionBank.setCreatedBy(requestPayload.getCreatedBy());
            newQuestionBank.setSubjectId(requestPayload.getSubjectId());

            questionBankRepo.save(newQuestionBank);

            responseStatus = "success";
            responseMessage = "Successfully Insert";

            addQuestionBankResponse.put("responseStatus", responseStatus);
            addQuestionBankResponse.put("responseMessage", responseMessage);



        }catch (Exception e){
            logger.error("Exception while add question banks :: {}" , e.getMessage() );
            responseStatus = "fail";
            responseMessage = e.getMessage();

            addQuestionBankResponse.put("responseStatus", responseStatus);
            addQuestionBankResponse.put("responseMessage", responseMessage);

        }

        return  addQuestionBankResponse;
    }

    public JSONObject addQuestionBanksArray(List<QuestionBankInsertRequest> requestPayload){
        JSONObject addQuestionBankResponse = new JSONObject();
        String responseStatus;
        String responseMessage;
        try{
            logger.debug("Trying to insert Question Banks in Arrays");

            int arraySize = requestPayload.size();

            if(arraySize == 0){
                logger.debug("Array Size is 0");
                addQuestionBankResponse = ResponseUtils.responseFormatUtils("0", "No new records to insert");
                return addQuestionBankResponse;
            }

            int recordSaveCount = 0;

            for(int i =0; i < arraySize; i++){
                insertQuestionAndOptionBank(requestPayload, i);
                recordSaveCount ++;
            }

            responseStatus = "0";
            responseMessage = "Successfully Insert " + recordSaveCount + " records" ;
            addQuestionBankResponse = ResponseUtils.responseFormatUtils(responseStatus, responseMessage);

        }catch (Exception e){
            logger.error("Exception while add question banks :: {}" , e.getMessage());
            responseStatus = "1";
            responseMessage = e.getMessage();

            addQuestionBankResponse = ResponseUtils.responseFormatUtils(responseStatus, responseMessage);
        }

        return  addQuestionBankResponse;

    }

    public void insertQuestionAndOptionBank(List<QuestionBankInsertRequest> questionLists, int questionBankIndex) {

        try {
            QuestionBankInsertRequest questionBank = questionLists.get(questionBankIndex);

            QuestionBankDomain newQuestionBank = new QuestionBankDomain();
            newQuestionBank.setQuestionType(questionBank.getQuestionType());
            newQuestionBank.setQuestionContent(questionBank.getQuestionContent());
            newQuestionBank.setDifficulty(questionBank.getDifficulty());
            newQuestionBank.setCreatedBy(questionBank.getCreatedBy());
            newQuestionBank.setSubjectId(questionBank.getSubjectId());
            newQuestionBank.setChapterId(questionBank.getChapterId());

            int optionListSize = questionBank.getOptionLists().size();

            questionBankRepo.save(newQuestionBank);
            logger.debug("Successfully Save Question Info :: {}", newQuestionBank.getId());

            for(int i = 0; i < optionListSize; i++){
                OptionBankInsertRequest optionBank = questionBank.getOptionLists().get(i);
                OptionBankDomain newOptionBank = new OptionBankDomain();
                newOptionBank.setOptionText(optionBank.getOptionText());
                newOptionBank.setIsCorrect(optionBank.getIsCorrect());
                newOptionBank.setQuestionId(newQuestionBank.getId());

                optionBankRepo.save(newOptionBank);
                logger.debug("Successfully Save Question Info :: {}", newOptionBank);
            }

            logger.debug("successfully save question and options banks");
        }catch (Exception e){
            logger.error("Exception while saving questions and option banks :: {}", e.getMessage());
        }
    }
}
