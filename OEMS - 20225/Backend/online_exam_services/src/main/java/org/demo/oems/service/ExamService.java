package org.demo.oems.service;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.*;
import org.demo.oems.payload.request.CreateExamRequest;
import org.demo.oems.payload.request.ExamPaperGenerationRequest;
import org.demo.oems.payload.response.GetExamListsResponse;
import org.demo.oems.payload.response.OptionListResponse;
import org.demo.oems.payload.response.QuestionBankListsResponse;
import org.demo.oems.repository.*;
import org.demo.oems.utils.ArrayStringUtils;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.DateUtils;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.demo.oems.utils.CommonConstantUtils.*;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final QuestionBankRepo questionBankRepo;

    private final OptionService optionService;

    private final SubjectRepo subjectRepo;

    private final SubjectChapterRepo chapterRepo;

    private final ClassRepo classRepo;

    private final ExamRepo examRepo;

    private final ExamPaperRepo examPaperRepo;

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

    //===========================================================================
    //1. Create Exam Services
    //===========================================================================
    @Transactional
    public Map<String, Object> createExam(CreateExamRequest createExamRequest) {
        logger.debug("Start - createExam with request :: {}", createExamRequest);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{

            LocalDateTime examDate = DateUtils.formatTimestamp(createExamRequest.getExamDate());

            int validateExamDate = DateUtils.compareDateWithCurrent(examDate);
            if(validateExamDate != 1){
                logger.error("Exam date cannot be before current date");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Exam date is already passed", "");
                return finalServiceResponse;
            }

            logger.debug("Step 1 :: Validate Subject ID");
            Optional<SubjectDomain> subjectDomainOptional = subjectRepo.findById(createExamRequest.getSubjectId());

            if(subjectDomainOptional.isEmpty()){
                logger.error("Subject not found");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Subject Info not found", "");
                return finalServiceResponse;
            }

            logger.debug("Step 2 :: Validate Class ID");
            Optional<ClassDomain> classDomainOptional = classRepo.findById(createExamRequest.getClassId());
            if(classDomainOptional.isEmpty()){
                logger.error("Subject not found");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Class Info not found", "");
                return finalServiceResponse;
            }

            logger.debug("Step 3 :: Save Exam Paper Attributes");
            ExamPaperDomain newExamPaper = this.createOrUpdateExamPaperDomain(createExamRequest, true);

            if(newExamPaper == null){
                logger.error("Failed to create exam paper attributes");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Failed to create exam paper info", "");
                return finalServiceResponse;
            }

            long examPaperId = newExamPaper.getId();
            logger.debug("Successfully Create New Exam Paper with ID :: {}", examPaperId);

            //check if manual or auto
            logger.debug("Step 4 :: Save Exam Info");
            createExamRequest.setExamPaperId(examPaperId);
            ExamDomain newExamDomain = this.createOrUpdateExamInfoDomain(createExamRequest, true);

            if(newExamDomain == null){
                logger.error("Failed to create exam info");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Failed to create exam info", "");
                return finalServiceResponse;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("newExamInfo", newExamDomain);
            data.put("newEamPaper", newExamPaper);

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully created exam info", data);

            logger.debug(LOG_PREFIX_FINAL_SERVICE_RESPONSE, finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE,"createExam", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    //===========================================================================
    //2. Update Exam Services
    //===========================================================================
    @Transactional
    public Map<String, Object> updateExam(CreateExamRequest createExamRequest) {
        logger.debug("Start - createExam with request :: {}", createExamRequest);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            LocalDateTime examDate = DateUtils.formatTimestamp(createExamRequest.getExamDate());

            int validateExamDate = DateUtils.compareDateWithCurrent(examDate);
            if(validateExamDate != 1){
                logger.error("Exam date cannot be before current date");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Exam date is already passed", "");
                return finalServiceResponse;
            }

            logger.debug("Step 1 :: Validate Subject ID");
            Optional<SubjectDomain> subjectDomainOptional = subjectRepo.findById(createExamRequest.getSubjectId());

            if(subjectDomainOptional.isEmpty()){
                logger.error("Subject not found");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Subject Info not found", "");
                return finalServiceResponse;
            }

            logger.debug("Step 2 :: Validate Class ID");
            Optional<ClassDomain> classDomainOptional = classRepo.findById(createExamRequest.getClassId());
            if(classDomainOptional.isEmpty()){
                logger.error("Subject not found");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Class Info not found", "");
                return finalServiceResponse;
            }

            logger.debug("Step 3 :: Update Exam Paper Attributes");
            ExamPaperDomain updateExamPaperDomain = this.createOrUpdateExamPaperDomain(createExamRequest, false);

            if(updateExamPaperDomain == null){
                logger.error("Failed to update Exam Paper Attributes");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Failed to update Exam Paper Info", "");
                return finalServiceResponse;
            }

            logger.debug("Step 4 :: Update Exam Info");
            ExamDomain updateExamDomain = this.createOrUpdateExamInfoDomain(createExamRequest, false);

            if(updateExamDomain == null){
                logger.error("Failed to update Exam Paper Attributes");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Failed to update Exam Info", "");
                return finalServiceResponse;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("updateExamInfo", updateExamDomain);
            data.put("updateExamPaper", updateExamPaperDomain);

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Update", data);

            logger.debug(LOG_PREFIX_FINAL_SERVICE_RESPONSE, finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE,"updateExam", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    //===========================================================================
    //3. Delete Exam Info
    //===========================================================================
    public Map<String, Object> deleteExam(long examId) {
        logger.debug("Start - deleteExam with request :: {}", examId);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Step 1 :: Validate Exam ID");
            Optional<ExamDomain> examDomainOptional = examRepo.findById(examId);
            if(examDomainOptional.isEmpty()){
                logger.error("Exam Info not found");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Exam info not found", "");
                return finalServiceResponse;
            }

            examRepo.deleteById(examDomainOptional.get().getId());
            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Delete", "");

            logger.debug(LOG_PREFIX_FINAL_SERVICE_RESPONSE, finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE,"deleteExam", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    //===========================================================================
    //4. Get All Exam Info
    //===========================================================================
    public Map<String, Object> getAllExams() {
        logger.debug("Start - getAllExams");
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Step 1 :: Get All Exams Info");
            List<ExamDomain> examLists = examRepo.findAll();

            List<GetExamListsResponse> examListsInfoResponse = buildExamListsResponse(examLists);

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Get All Exams", examListsInfoResponse);
            logger.debug(LOG_PREFIX_FINAL_SERVICE_RESPONSE, finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE,"deleteExam", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    //===========================================================================
    //5. Get All Exam Info by Teacher ID
    //===========================================================================
    public Map<String, Object> getAllExamsByTeacherId(String teacherId) {
        logger.debug("Start - getAllExamsByTeacherId with Teacher ID :: {}", teacherId);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        List<GetExamListsResponse> examListsResponses = new ArrayList<>();
        List<GetExamListsResponse> finalExamListsResponse = new ArrayList<>();
        try{
            logger.debug("Step 1 :: Get class and subject the teacher is teaching");
            List<ClassDomain> classLists = classRepo.getClassDomainsByTeacherIdEqualsIgnoreCase(teacherId);

            if (classLists.isEmpty()) {
                logger.error("No classes found for teacher ID :: {}", teacherId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "No classes found", "");
                return finalServiceResponse;
            }

            for(ClassDomain classDomain : classLists){
                long subjectId = classDomain.getSubjectId();
                long classId = classDomain.getClassId();

                logger.debug("Step 2 :: Find Exam Info Related to Class ID {} and Subject ID {}", classId, subjectId);
                List<ExamDomain> examLists = examRepo.getExamDomainsByClassIdAndSubjectId(subjectId, classId);

                examListsResponses = buildExamListsResponse(examLists);
                finalExamListsResponse.addAll(examListsResponses);
            }
            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Get All Exams", finalExamListsResponse);
            logger.debug(LOG_PREFIX_FINAL_SERVICE_RESPONSE, finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE,"getAllExamsByTeacherId", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public List<GetExamListsResponse> buildExamListsResponse(List<ExamDomain> examDomainList){
        logger.debug("Start - buildExamListsResponse :: {}", examDomainList);
        List<GetExamListsResponse> examListsResponses = new ArrayList<>();
        for(ExamDomain examDomain : examDomainList){
            logger.debug("Step 2 :: Find Exam Paper Info");
            GetExamListsResponse examInfoResponse = new GetExamListsResponse();

            examInfoResponse.setExamId(examDomain.getId());
            examInfoResponse.setClassId(examDomain.getClassId());
            examInfoResponse.setSubjectId(examDomain.getSubjectId());

            String examDate = DateUtils.convertTimestampToString(examDomain.getExamDate());
            examInfoResponse.setExamDate(examDate);
            examInfoResponse.setExamTitle(examDomain.getExamTitle());
            examInfoResponse.setDuration(examDomain.getDuration());

            ExamPaperDomain examPaperDomain = this.findExamPaperDomainById(examDomain.getExamPaperId());
            if(examPaperDomain != null){
                logger.debug("Exam Paper Info Exists :: {}", examPaperDomain);
                examInfoResponse.setExamPaperId(examPaperDomain.getId());

                examInfoResponse.setExamPaperType(examPaperDomain.getExamPaperType());
                examInfoResponse.setEasyQuestions(examPaperDomain.getEasyQuestions());
                examInfoResponse.setMediumQuestions(examPaperDomain.getMediumQuestions());
                examInfoResponse.setHardQuestions(examPaperDomain.getHardQuestions());

                int[] questionIdArrayString = ArrayStringUtils.stringToIntArray(examPaperDomain.getQuestionIdArrayString());

                examInfoResponse.setQuestionIds(questionIdArrayString);
                examInfoResponse.setExamPaperStatus(examPaperDomain.getExamPaperStatus());
            }

            examListsResponses.add(examInfoResponse);
        }

        logger.debug("Exam Info Response Lists :: {}",examListsResponses);
        return examListsResponses;
    }


    public ExamDomain findExamDomainById(Long examId){
        if(examId == null){
            return null;
        }else{
            Optional<ExamDomain> examDomainOptional = examRepo.findById(examId);
            return examDomainOptional.orElse(null);
        }
    }

    public ExamDomain createOrUpdateExamInfoDomain(CreateExamRequest createExamRequest, boolean isCreated){
        try {
            logger.debug("Start - createOrUpdateExamInfoDomain :: {}", createExamRequest);
            ExamDomain newExamDomain = new ExamDomain();

            if(!isCreated){
                logger.debug("Update Operations");
                newExamDomain = findExamDomainById(createExamRequest.getExamPaperId());

                if(newExamDomain == null){
                    logger.error("Exam info record is not found :: {}", createExamRequest.getExamPaperId());
                    return null;
                }

            }

            newExamDomain.setClassId(createExamRequest.getClassId());
            newExamDomain.setSubjectId(createExamRequest.getSubjectId());

            LocalDateTime examDate = DateUtils.formatTimestamp(createExamRequest.getExamDate());

            newExamDomain.setExamDate(examDate);
            newExamDomain.setExamTitle(createExamRequest.getExamTitle());
            newExamDomain.setDuration(createExamRequest.getDuration());
            newExamDomain.setExamPaperId(createExamRequest.getExamPaperId());

            examRepo.save(newExamDomain);

            logger.debug("Successfully Create or Update the exam info attributes :: {}", newExamDomain);
            return newExamDomain;
        }catch (Exception e){
            logger.error("Exception while trying to create or update exam info :: {}", e.getMessage());
            return null;
        }

    }

    public ExamPaperDomain findExamPaperDomainById(Long examPaperId){
        logger.debug("Start - findExamPaperDomainById :: {}", examPaperId);
        if(examPaperId == null){
            return null;
        }else{
            Optional<ExamPaperDomain> examPaperDomainOptional = examPaperRepo.findById(examPaperId);
            return examPaperDomainOptional.orElse(null);
        }
    }

    public ExamPaperDomain createOrUpdateExamPaperDomain(CreateExamRequest createExamRequest, boolean isCreated){
        try {
            logger.debug("Start - createOrUpdateExamDomain :: {}", createExamRequest);
            ExamPaperDomain newExamPaperDomain = new ExamPaperDomain();

            if(!isCreated){
                logger.debug("Update Operations");
                newExamPaperDomain = findExamPaperDomainById(createExamRequest.getExamPaperId());

                if(newExamPaperDomain == null){
                    logger.error("Exam Paper record is not found :: {}", createExamRequest.getExamPaperId());
                    return null;
                }

            }

            String examPaperStatus = Boolean.TRUE.equals(createExamRequest.getIsDraft()) ? VALUE_DRAFT : VALUE_PUBLISHED;
            newExamPaperDomain.setExamPaperStatus(examPaperStatus);
            newExamPaperDomain.setExamPaperType(createExamRequest.getExamPaperType());

            if (createExamRequest.getExamPaperType().equalsIgnoreCase(VALUE_MANUAL)) {
                logger.debug("Exam Paper Type is manual");
                newExamPaperDomain.setExamPaperType(VALUE_MANUAL);

                String questionIdsString = ArrayStringUtils.intArrayToString(createExamRequest.getQuestionIds());
                newExamPaperDomain.setQuestionIdArrayString(questionIdsString);
            } else {
                logger.debug("Exam Paper Type is Default or Auto");
                newExamPaperDomain.setExamPaperType(VALUE_AUTO);
                newExamPaperDomain.setEasyQuestions(createExamRequest.getEasyQuestions());
                newExamPaperDomain.setMediumQuestions(createExamRequest.getMediumQuestions());
                newExamPaperDomain.setHardQuestions(createExamRequest.getHardQuestions());
            }

            examPaperRepo.save(newExamPaperDomain);

            logger.debug("Successfully Create or Update the exam paper attributes :: {}", newExamPaperDomain);
            return newExamPaperDomain;
        }catch (Exception e){
            logger.error("Exception while trying to create or update exam paper :: {}", e.getMessage());
            return null;
        }
    }






}
