package org.demo.oems.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.*;
import org.demo.oems.payload.request.CreateExamRequest;
import org.demo.oems.payload.request.ExamPaperGenerationRequest;
import org.demo.oems.payload.request.TakeExamRequest;
import org.demo.oems.payload.response.*;
import org.demo.oems.repository.*;
import org.demo.oems.utils.*;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.demo.oems.utils.CommonConstantUtils.*;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final QuestionBankRepo questionBankRepo;

    private final OptionService optionService;

    private final QuestionBankService questionBankService;

    private final SubjectService subjectService;

    private final ClassroomService classroomService;

    private final SubjectRepo subjectRepo;

    private final SubjectChapterRepo chapterRepo;

    private final ClassRepo classRepo;

    private final ExamRepo examRepo;

    private final ExamPaperRepo examPaperRepo;

    private static final Logger logger = LogManager.getLogger(ExamService.class);
    private final ClassroomRepo classroomRepo;
    private final UserInfoRepo userInfoRepo;
    private final ExamSessionRepo examSessionRepo;

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
    @Transactional
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

            long examPaperId = examDomainOptional.get().getExamPaperId();
            logger.debug("Step 2 :: Delete Exam Paper with ID :: {}", examPaperId);
            examPaperRepo.deleteById(examPaperId);

            logger.debug("Step 3 :: Delete Exam with ID :: {}", examDomainOptional.get().getId());
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

            String examStatus = DateUtils.getExamStatus(examDomain.getExamDate(), examDomain.getDuration());
            logger.debug("exam status :: {}", examStatus);
            examInfoResponse.setExamStatus(examStatus);

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

                logger.debug("Exam Info Response :: {}", examInfoResponse);
            }

            examListsResponses.add(examInfoResponse);
        }

        logger.debug("Exam Info Response Lists :: {}",examListsResponses);
        return examListsResponses;
    }


    public ExamDomain findExamDomainById(Long examId){
        logger.debug("Start - findExamDomainById :: {}", examId);
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

    //===========================================================================
    //6. Get All Exam Info by Teacher ID
    //===========================================================================
    public Map<String, Object> getAllExamsByStudentId(String studentId) {
        logger.debug("Start - getAllExamsByStudentId with student ID :: {}", studentId);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        List<GetExamListsResponse> examListsResponses = new ArrayList<>();
        List<GetExamListsResponse> finalExamListsResponse = new ArrayList<>();
        try{
            logger.debug("Step 1 :: Get All Classes enrollment with student ID");
            List<ClassroomDomain> classEnrollmentLists = classroomRepo.findClassroomDomainsByStudentIdAndStatus(studentId, VALUE_APPROVED);

            if (classEnrollmentLists.isEmpty()) {
                logger.error("No classes enrollment found for student ID:: {}", studentId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "No class enrollments found", "");
                return finalServiceResponse;
            }


            for(ClassroomDomain classEnrollment : classEnrollmentLists){
                long classId = classEnrollment.getClassId();

                List<ExamDomain> examDomainList = examRepo.getExamDomainsByClassId(classId);
                examListsResponses = buildExamListsResponse(examDomainList);
                finalExamListsResponse.addAll(examListsResponses);
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Get All Exams", finalExamListsResponse);
            logger.debug(LOG_PREFIX_FINAL_SERVICE_RESPONSE, finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE,"getAllExamsByStudentId", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return finalServiceResponse;
        }

    }

    public List<ExamDomain> findAllCompletedExamLists(long classId){
        return examRepo.getExamDomainsByClassIdAndExamDateIsAfter(classId, LocalDateTime.now());
    }

    public Map<String, Object> getExamPaperForStudent(TakeExamRequest takeExamRequest) {
        Map<String, Object> finalServiceResponse = new HashMap<>();

        try {
            logger.debug("Start - getExamPaperForStudent :: {}", takeExamRequest);

            // Step 1: Validate student
            UserInfoDomain student = userInfoRepo.findUserInfoDomainByUserId(takeExamRequest.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));

            // Step 2: Find exam
            ExamDomain exam = findExamDomainById(takeExamRequest.getExamId());
            if (exam == null) {
                return ResponseUtils.formatAPIResponse("400", "Exam not found", null);
            }

            // Step 3: Validate exam time (skip for demo)
            if (Boolean.FALSE.equals(takeExamRequest.getIsDemo())) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime start = exam.getExamDate();
                LocalDateTime end = start.plusMinutes(exam.getDuration());

                if (now.isBefore(start)) return ResponseUtils.formatAPIResponse("400", "Exam not started yet", null);
                if (now.isAfter(end))   return ResponseUtils.formatAPIResponse("400", "Exam already finished", null);
            }

            // Step 4: Check if session already exists
            ExamSessionDomain session = getExamSessionByExamIdAndUserId(exam.getId(), student.getUserId());

            ExamPaperResponse examPaperResponse;

            if (session != null && StringUtils.isNotBlank(session.getProgressData())) {
                // Session exists + has saved progress → restore from JSON
                logger.debug("Restoring exam paper from saved progress");
                examPaperResponse = restoreExamPaperFromProgress(session.getProgressData());
            } else {
                // First time → build fresh exam paper
                logger.debug("Building new exam paper");
                examPaperResponse = buildNewExamPaperResponse(exam);
                examPaperResponse.setStudentId(student.getUserId());
                examPaperResponse.setStudentName(student.getName());

                // Create or update session with initial progress
                if (session == null) {
                    session = new ExamSessionDomain();
                    session.setStudent(student);
                    session.setExam(exam);
                }
                session.setStartTime(LocalDateTime.now());
                session.setLastSave(LocalDateTime.now());
                session.setStatus(VALUE_EXAM_IN_PROGRESS);
                session.setIpAddress(takeExamRequest.getIpAddress());
                session.setProgressData(toJson(examPaperResponse));  // Save full response as JSON

                examSessionRepo.save(session);
            }

            // Final response
            examPaperResponse.setExamSessionId(session.getId());
            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", examPaperResponse);

            return finalServiceResponse;

        } catch (Exception e) {
            logger.error("Error in getExamPaperForStudent", e);
            return ResponseUtils.formatAPIResponse("500", "Internal error: " + e.getMessage(), null);
        }
    }

    private ExamPaperResponse buildNewExamPaperResponse(ExamDomain exam) {
        ExamPaperResponse resp = new ExamPaperResponse();

        // Common fields (already good)
        resp.setExamId(exam.getId());
        resp.setExamTitle(exam.getExamTitle());
        resp.setExamDuration(exam.getDuration());
        resp.setClassId(exam.getClassId());
        resp.setSubjectId(exam.getSubjectId());

        // Subject & Class name (optional enrichment)
        SubjectDomain subjectInfo = subjectService.getSubjectInfoById(exam.getSubjectId());
        if(subjectInfo != null){
            resp.setSubjectId(exam.getSubjectId());
            resp.setSubjectName(subjectInfo.getSubjectName());
        }

        ClassDomain classInfo = classroomService.getClassInfoById(exam.getClassId());
        if(classInfo != null){
            resp.setClassId(exam.getClassId());
            resp.setClassName(classInfo.getClassName());
        }

        Long examPaperId = exam.getExamPaperId();
        ExamPaperDomain paper = examPaperRepo.findById(examPaperId)
                .orElseThrow(() -> new IllegalStateException("Exam paper not found"));

        String paperType = paper.getExamPaperType();

        List<ExamPaperQuestionResponse> questionResponseList = new ArrayList<>();

        if ("MANUAL".equalsIgnoreCase(paperType)) {
            // MANUAL mode
            int[] qIds = ArrayStringUtils.stringToIntArray(paper.getQuestionIdArrayString());

            for (long qId : qIds) {
                QuestionBankDomain q = questionBankService.findQuestionBankById(qId);
                if (q == null) continue;

                ExamPaperQuestionResponse qr = buildQuestionResponse(q);
                questionResponseList.add(qr);
            }
        } else if ("AUTO".equalsIgnoreCase(paperType)) {
            // AUTO mode – select random questions by difficulty
            logger.debug("Building AUTO exam paper for subjectId: {}", exam.getSubjectId());

            // Fetch random questions for each difficulty
            List<QuestionBankDomain> easyQuestions = questionBankRepo.findRandomNativeByDifficultyAndSubjectId(
                    "EASY", paper.getEasyQuestions(), exam.getSubjectId());

            List<QuestionBankDomain> mediumQuestions = questionBankRepo.findRandomNativeByDifficultyAndSubjectId(
                    "MEDIUM", paper.getMediumQuestions(), exam.getSubjectId());

            List<QuestionBankDomain> hardQuestions = questionBankRepo.findRandomNativeByDifficultyAndSubjectId(
                    "HARD", paper.getHardQuestions(), exam.getSubjectId());

            // Combine all selected questions
            List<QuestionBankDomain> allSelected = new ArrayList<>();
            allSelected.addAll(easyQuestions);
            allSelected.addAll(mediumQuestions);
            allSelected.addAll(hardQuestions);

            // Shuffle the entire list (random order for student)
            Collections.shuffle(allSelected);

            // Build response DTO for each question
            for (QuestionBankDomain q : allSelected) {
                ExamPaperQuestionResponse qr = buildQuestionResponse(q);
                questionResponseList.add(qr);
            }

            logger.debug("AUTO mode generated {} questions (Easy: {}, Medium: {}, Hard: {})",
                    questionResponseList.size(), easyQuestions.size(), mediumQuestions.size(), hardQuestions.size());
        } else {
            throw new IllegalArgumentException("Unsupported exam paper type: " + paperType);
        }

        // Final shuffle of questions (already shuffled in AUTO, but consistent)
        if (questionResponseList.size() > 1) {
            Collections.shuffle(questionResponseList);
        }

        resp.setQuestionLists(questionResponseList);

        return resp;
    }

    /**
     * Reusable helper to convert QuestionBankDomain → ExamPaperQuestionResponse
     */
    private ExamPaperQuestionResponse buildQuestionResponse(QuestionBankDomain q) {
        ExamPaperQuestionResponse qr = new ExamPaperQuestionResponse();
        qr.setQuestionId(q.getId());
        qr.setQuestionText(q.getQuestionContent());
        qr.setQuestionType(q.getQuestionType().name());
        qr.setChapterName(q.getChapter().getChapter());
        qr.setChapterId(q.getChapter().getId());

        // Fetch and prepare options
        List<OptionBankDomain> opts = optionService.getOptionListsByQuestionId(q.getId());
        List<ExamPaperOptionResponse> optionResponses = getExamPaperOptionResponses(opts);

        // Shuffle options if MCQ or True/False (to prevent order bias)
        if (QuestionBankDomain.QuestionType.MULTIPLE_CHOICE.name().equals(qr.getQuestionType()) ||
                QuestionBankDomain.QuestionType.TRUE_FALSE.name().equals(qr.getQuestionType())) {
            Collections.shuffle(optionResponses);
        }

        qr.setOptionLists(optionResponses);

        // Student has not answered yet
        qr.setStudentAnswer(null);

        return qr;
    }


    // Convert ExamPaperResponse → JSON string for storage
    private String toJson(ExamPaperResponse resp) {
        try {
            return new ObjectMapper().writeValueAsString(resp);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize exam paper to JSON", e);
            return "{}";
        }
    }

    // Restore from saved JSON
    private ExamPaperResponse restoreExamPaperFromProgress(String progressJson) {
        try {
            return new ObjectMapper().readValue(progressJson, ExamPaperResponse.class);
        } catch (Exception e) {
            logger.error("Failed to parse saved progress JSON", e);
            return new ExamPaperResponse(); // fallback empty
        }
    }

    public ExamSessionDomain getExamSessionByExamIdAndUserId(long examId, String userId){
        Optional<ExamSessionDomain> examSessionOptional = examSessionRepo.findExamSessionDomainByExam_IdAndStudent_UserId(examId, userId);
        return examSessionOptional.orElse(null);
    }

    private static List<ExamPaperOptionResponse> getExamPaperOptionResponses(List<OptionBankDomain> optionDomainLists) {
        logger.debug("Start - getExamPaperOptionResponses");

        List<ExamPaperOptionResponse> optionResponseLists = new ArrayList<>();
        for(OptionBankDomain optionBankDomain : optionDomainLists){
            ExamPaperOptionResponse examPaperOption = new ExamPaperOptionResponse();

            examPaperOption.setOptionText(optionBankDomain.getOptionText());
            examPaperOption.setOptionId(optionBankDomain.getId());

            optionResponseLists.add(examPaperOption);
        }

        if(optionResponseLists.size() > 1){
            logger.debug("Has more than 2 option go shuffle");
            return ShuffleUtils.shuffleList(optionResponseLists);
        }

       return optionResponseLists;
    }

    ExamSessionDomain findExamSessionById(long examSessionId) {
        Optional<ExamSessionDomain> examSessionDomainOptional = examSessionRepo.findById(examSessionId);
        return examSessionDomainOptional.orElse(null);
    }

    @Transactional
    public Map<String, Object> saveExamPaperProgress(ExamPaperResponse examPaper) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        SaveExamProgressResponse saveExamProgressResponse = new SaveExamProgressResponse();
        // 1. Find session
        ExamSessionDomain session = findExamSessionById(examPaper.getExamSessionId());
        if(session == null) {
            finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Exam session not found", "");
            return finalServiceResponse;
        }

        // Step 1: Validate student
        UserInfoDomain currentStudent = userInfoRepo.findUserInfoDomainByUserId(examPaper.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // 2. Authorization: only owner
        if (!session.getStudent().getUserId().equals(currentStudent.getUserId())) {
            throw new AccessDeniedException("Not authorized");
        }

        // 3. Optional: check exam still active
        ExamDomain exam = session.getExam();
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(exam.getExamDate().plusMinutes(exam.getDuration()))) {
            throw new IllegalStateException("Exam time has ended – cannot save progress");
        }

        // 4. Serialize full paper + answers to JSON
        String progressJson;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            progressJson = objectMapper.writeValueAsString(examPaper.getQuestionLists());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize exam progress", e);
        }

        // 5. Update session
//        session.setProgressData(progressJson);
        session.setProgressData(toJson(examPaper));
        session.setLastSave(now);
        session.setStatus(VALUE_EXAM_IN_PROGRESS);

        examSessionRepo.save(session);

        saveExamProgressResponse.setExamSessionId(session.getId());
        saveExamProgressResponse.setStatus("saved");
        saveExamProgressResponse.setLastSaved(now);
        saveExamProgressResponse.setMessage("Progress saved successfully");

        finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", saveExamProgressResponse);
        return finalServiceResponse;
    }

    /**
     * Final submission + auto-grading
     */
    @Transactional
    public  Map<String, Object> submitExam(ExamPaperResponse request) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        SubmitExamResponse submitExamResponse = new SubmitExamResponse();

        // Step 1: Validate student
        UserInfoDomain currentStudent = userInfoRepo.findUserInfoDomainByUserId(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // 1. Find session
        ExamSessionDomain session = findExamSessionById(request.getExamSessionId());
        if(session == null) {
            finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Exam session not found", "");
            return finalServiceResponse;
        }

        // 2. Security check
        if (!session.getStudent().getId().equals(currentStudent.getId())) {
            throw new AccessDeniedException("You are not authorized to submit this exam");
        }

        // 3. Prevent double submit
        if ("submitted".equals(session.getStatus()) || "graded".equals(session.getStatus())) {
            throw new IllegalStateException("Exam has already been submitted");
        }

        // 4. Check time (allow late submit with flag)
        ExamDomain exam = session.getExam();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = exam.getExamDate().plusMinutes(exam.getDuration());
        boolean isLate = now.isAfter(endTime);

        // 5. Update progress (if client sent latest answers)
        String finalProgressJson = null;
        if (request.getQuestionLists() != null && !request.getQuestionLists().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                finalProgressJson = objectMapper.writeValueAsString(request.getQuestionLists());
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize final answers", e);
                throw new RuntimeException("Failed to process submission", e);
            }
        } else if (StringUtils.isNotBlank(session.getProgressData())) {
            finalProgressJson = session.getProgressData(); // use last saved
        } else {
            throw new IllegalStateException("No progress data available for submission");
        }

        // 6. Auto-grade the exam
        GradingResult gradingResult = autoGradeExam(finalProgressJson, exam);

        // 7. Finalize session
        session.setProgressData(finalProgressJson);
        session.setStatus(VALUE_EXAM_SUBMITTED);
        session.setLastSave(now);
        session.setSubmitTime(now);
        session.setScore(gradingResult.getObtainedScore());


        // Assuming exam.getExamDate() returns a LocalDateTime
        LocalDateTime examDate = exam.getExamDate();
        long timeTakenSecond = Duration.between(examDate, LocalDateTime.now()).toSeconds();

        session.setTimeTaken(timeTakenSecond);
        examSessionRepo.save(session);

        submitExamResponse = SubmitExamResponse.builder()
                .examSessionId(session.getId())
                .status(session.getStatus())
                .submittedAt(now)
                .obtainedScore(gradingResult.getObtainedScore())
                .totalPossibleScore(gradingResult.getTotalPossibleScore())
                .answeredCount(gradingResult.getAnsweredCount())
                .totalQuestions(gradingResult.getTotalQuestions())
                .message(isLate ? "Exam submitted late" : "Exam submitted successfully")
                .isLate(isLate)
                .build();

        finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", submitExamResponse);
        return finalServiceResponse;
    }

    /**
     * Auto-grading logic (MCQ, True/False, Fill-in-Blank)
     */
    @Transactional(readOnly = true)
    public GradingResult autoGradeExam(String progressJson, ExamDomain exam) {
        List<QuestionGradeDetail> details = new ArrayList<>();
        int totalPossible = 0;
        int obtained = 0;
        int answered = 0;

        try {
            // Parse saved progress JSON
            ObjectMapper mapper = new ObjectMapper();
            List<ExamPaperQuestionResponse> submittedQuestions = mapper.readValue(
                    progressJson,
                    new TypeReference<>() {}
            );

            int totalQuestions = submittedQuestions.size();

            for (ExamPaperQuestionResponse submitted : submittedQuestions) {
                Long qId = submitted.getQuestionId();
                QuestionBankDomain original = questionBankRepo.findById(qId).orElse(null);

                if (original == null) {
                    logger.warn("Question not found during grading: {}", qId);
                    continue;
                }

                int questionPoints = original.getPoints();
                totalPossible += questionPoints;

                String studentAnswer = submitted.getStudentAnswer();
                if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
                    details.add(QuestionGradeDetail.builder()
                            .questionId(qId)
                            .questionType(original.getQuestionType().name())
                            .pointsPossible(questionPoints)
                            .pointsObtained(0)
                            .isCorrect(false)
                            .studentAnswer(null)
                            .correctAnswer("N/A")
                            .build());
                    continue;
                }

                answered++;

                boolean isCorrect = false;
                String correctAnswerDisplay = "";

                QuestionBankDomain.QuestionType type = original.getQuestionType();

                // Fetch correct option(s)
                OptionBankDomain correctOptions = optionService.findByQuestionIdAndIsCorrectTrue(qId);

                if (type == QuestionBankDomain.QuestionType.MULTIPLE_CHOICE || type == QuestionBankDomain.QuestionType.TRUE_FALSE) {

                    String correctOptionId = correctOptions.getId().toString();

                    isCorrect = correctOptionId.equalsIgnoreCase(studentAnswer.trim());
                    correctAnswerDisplay = correctOptions.getOptionText();

                }
                else if (type == QuestionBankDomain.QuestionType.FILL_BLANK) {
                    // Exact match (case-insensitive, trimmed)
                    String correctText = correctOptions.getCorrectAnswer().trim();
                    isCorrect = studentAnswer.trim().equalsIgnoreCase(correctText);
                    correctAnswerDisplay = correctText;
                }

                int pointsEarned = isCorrect ? questionPoints : 0;
                obtained += pointsEarned;

                details.add(QuestionGradeDetail.builder()
                        .questionId(qId)
                        .questionType(type.name())
                        .pointsPossible(questionPoints)
                        .pointsObtained(pointsEarned)
                        .isCorrect(isCorrect)
                        .studentAnswer(studentAnswer)
                        .correctAnswer(correctAnswerDisplay)
                        .build());
            }

            String summary = String.format("%d/%d - %s", obtained, totalPossible,
                    obtained >= totalPossible * 0.8 ? "Excellent!" : obtained >= totalPossible * 0.5 ? "Good" : "Needs Improvement");

            return GradingResult.builder()
                    .obtainedScore(obtained)
                    .totalPossibleScore(totalPossible)
                    .answeredCount(answered)
                    .totalQuestions(totalQuestions)
                    .summaryMessage(summary)
                    .details(details)   // can be omitted in response if too verbose
                    .build();

        } catch (Exception e) {
            logger.error("Auto-grading failed for exam: {}", exam.getId(), e);
            return GradingResult.builder()
                    .obtainedScore(0)
                    .totalPossibleScore(0)
                    .answeredCount(0)
                    .totalQuestions(0)
                    .summaryMessage("Grading failed – please contact support")
                    .build();
        }
    }


}
