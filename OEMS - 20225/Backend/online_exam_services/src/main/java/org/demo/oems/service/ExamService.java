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
import org.demo.oems.payload.request.TakeExamRequest;
import org.demo.oems.payload.response.*;
import org.demo.oems.repository.*;
import org.demo.oems.utils.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.demo.oems.utils.CommonConstantUtils.*;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamResultRepo examResultRepo;

    private final QuestionBankRepo questionBankRepo;

    private final SubjectService subjectService;

    private final ClassroomService classroomService;

    private final SubjectRepo subjectRepo;

    private final SubjectChapterRepo chapterRepo;

    private final ClassRepo classRepo;

    private final ExamRepo examRepo;

    private final ExamPaperRepo examPaperRepo;

    private final QuestionService questionService;

    private final AIGradingService aiGradingService;

    private static final Logger logger = LogManager.getLogger(ExamService.class);

    private final ClassroomRepo classroomRepo;

    private final UserInfoRepo userInfoRepo;

    private final ExamSessionRepo examSessionRepo;

    ExamSessionDomain findExamSessionById(long examSessionId) {
        Optional<ExamSessionDomain> examSessionDomainOptional = examSessionRepo.findById(examSessionId);
        return examSessionDomainOptional.orElse(null);
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
    public Map<String, Object> updateExam(long examId, CreateExamRequest createExamRequest) {
        logger.debug("Start - createExam with request :: {}", createExamRequest);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            createExamRequest.setExamId(examId);
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
                List<ExamDomain> examLists = examRepo.getExamDomainsByClassIdAndSubjectId(classId, subjectId);

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

    private ExamPaperResponse buildNewExamPaperResponse(ExamDomain exam) throws JsonProcessingException {
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
                QuestionBankDomain q = questionService.getQuestionDetailsById(qId);
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
    private ExamPaperQuestionResponse buildQuestionResponse(QuestionBankDomain questionBankDomain) throws JsonProcessingException {
        ExamPaperQuestionResponse qr = new ExamPaperQuestionResponse();
        qr.setQuestionId(questionBankDomain.getId());
        qr.setQuestionText(questionBankDomain.getQuestionContent());
        qr.setQuestionType(questionBankDomain.getQuestionType());
        qr.setChapterName(questionBankDomain.getChapter().getChapter());
        qr.setChapterId(questionBankDomain.getChapter().getId());
        ObjectMapper mapper = new ObjectMapper();
        List<String> optionResponses = mapper.readValue(
                questionBankDomain.getOptionContent(),
                new TypeReference<List<String>>() {}
        );
//        // Fetch and prepare options
//        List<OptionBankDomain> opts = optionService.getOptionListsByQuestionId(questionBankDomain.getId());
//        List<ExamPaperOptionResponse> optionResponses = getExamPaperOptionResponses(opts);

        // Shuffle options if MCQ or True/False (to prevent order bias)
        if (VALUE_MULTIPLE_CHOICE.equalsIgnoreCase(qr.getQuestionType()) ||
                VALUE_TRUE_FALSE.equalsIgnoreCase(qr.getQuestionType())) {
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

    @Transactional
    public Map<String, Object> saveExamPaperProgress(ExamPaperResponse examPaper) {
        logger.info("Start - saveExamPaperProgress with request :: {}", examPaper);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try {
            SaveExamProgressResponse saveExamProgressResponse = new SaveExamProgressResponse();
            // 1. Find session
            ExamSessionDomain session = findExamSessionById(examPaper.getExamSessionId());
            if (session == null) {
                logger.error("Exam session not found for ID :: {}", examPaper.getExamSessionId());
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Exam session not found", "");
                return finalServiceResponse;
            }

            // Step 1: Validate student
            UserInfoDomain currentStudent = userInfoRepo.findUserInfoDomainByUserId(examPaper.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));

            // 2. Authorization: only owner
            if (!session.getStudent().getUserId().equals(currentStudent.getUserId())) {
                logger.error("Unauthorized save attempt by user {} for session {}", currentStudent.getUserId(), session.getId());
                throw new AccessDeniedException("Not authorized");
            }

            // 3. Optional: check exam still active
//            ExamDomain exam = session.getExam();
//            LocalDateTime now = LocalDateTime.now();
//            if (now.isAfter(exam.getExamDate().plusMinutes(exam.getDuration()))) {
//                throw new IllegalStateException("Exam time has ended – cannot save progress");
//            }

            // 4. Serialize full paper + answers to JSON
            String progressJson;

            ObjectMapper objectMapper = new ObjectMapper();
            progressJson = objectMapper.writeValueAsString(examPaper.getQuestionLists());
            logger.debug("Saving progress JSON for session {}: {}", session.getId(), progressJson);

            // 5. Update session
            session.setProgressData(toJson(examPaper));
            session.setLastSave(LocalDateTime.now());
            session.setStatus(VALUE_EXAM_IN_PROGRESS);

            examSessionRepo.save(session);

            saveExamProgressResponse.setExamSessionId(session.getId());
            saveExamProgressResponse.setStatus("saved");
            saveExamProgressResponse.setLastSaved(LocalDateTime.now());
            saveExamProgressResponse.setMessage("Progress saved successfully");

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", saveExamProgressResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception in saveExamPaperProgress {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", "Internal error: " + e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    /**
     * Final submission + auto-grading
     */
    @Transactional
    public  Map<String, Object> submitExam(ExamPaperResponse request) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try {
            logger.info("Start - submitExam with request :: {}", request);
            SubmitExamResponse submitExamResponse = new SubmitExamResponse();

            // Step 1: Validate student
            UserInfoDomain currentStudent = userInfoRepo.findUserInfoDomainByUserId(request.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));

            // 1. Find session
            ExamSessionDomain session = findExamSessionById(request.getExamSessionId());
            if (session == null) {
                logger.error("Exam session not found for ID :: {}", request.getExamSessionId());
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Exam session not found", "");
                return finalServiceResponse;
            }

            // 2. Security check
            if (!session.getStudent().getId().equals(currentStudent.getId())) {
                logger.error("Unauthorized submit attempt by user {} for session {}", currentStudent.getUserId(), session.getId());
                throw new AccessDeniedException("You are not authorized to submit this exam");
            }

            // 3. Prevent double submit
            if ("submitted".equals(session.getStatus()) || "graded".equals(session.getStatus())) {
                logger.error("Exam session {} has already been submitted or graded", session.getId());
                throw new IllegalStateException("Exam has already been submitted");
            }

            // 4. Check time (allow late submit with flag)
            ExamDomain exam = session.getExam();
            LocalDateTime now = LocalDateTime.now();

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
            GradingResult gradingResult = this.autoGradeExam(finalProgressJson, exam, currentStudent.getUserId());

            // 7. Finalize session
            session.setProgressData(toJson(request));  // Save final answers
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
                    .message("Exam submitted successfully")
                    .questionGradeDetails(gradingResult.getDetails())
                    .build();

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", submitExamResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception in submitExam {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", "Internal error: " + e.getMessage(), "");
            return finalServiceResponse;
        }
    }

//    public GradingResult autoGradeExam(String progressJson, ExamDomain exam, String studentId) {
//
//        List<QuestionGradeDetail> details = new ArrayList<>();
//        int totalPossible = 0;
//        int obtained = 0;
//        int answered = 0;
//
//        boolean isScored = true;
//        boolean isFinal = true;
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//
//            List<ExamPaperQuestionResponse> submittedQuestions = mapper.readValue(
//                    progressJson,
//                    new TypeReference<List<ExamPaperQuestionResponse>>() {}
//            );
//
//            int totalQuestions = submittedQuestions.size();
//
//            // ✅ FIX: Batch fetch (avoid N+1 query problem)
//            List<Long> questionIds = submittedQuestions.stream()
//                    .map(ExamPaperQuestionResponse::getQuestionId)
//                    .toList();
//
//            Map<Long, QuestionBankDomain> questionMap =
//                    questionBankRepo.findAllById(questionIds)
//                            .stream()
//                            .collect(Collectors.toMap(QuestionBankDomain::getId, q -> q));
//
//            // 🔁 Loop through submitted answers
//            for (ExamPaperQuestionResponse submitted : submittedQuestions) {
//
//                Long qId = submitted.getQuestionId();
//                QuestionBankDomain question = questionMap.get(qId);
//
//                if (question == null) {
//                    logger.warn("Question not found: {}", qId);
//                    continue;
//                }
//
//                int questionPoints = question.getPoints();
//                totalPossible += questionPoints;
//
//                String studentAnswer = submitted.getStudentAnswer();
//                String correctAnswer = question.getCorrectAnswer();
//                String type = question.getQuestionType();
//
//                boolean isCorrect = false;
//                int pointsEarned = 0;
//                String correctAnswerDisplay = "";
//
//
//                // ❌ Not answered
//                if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
//                    details.add(QuestionGradeDetail.builder()
//                            .questionId(qId)
//                            .questionContent(question.getQuestionContent())
//                            .questionType(type)
//                            .questionDifficulty(question.getDifficulty())
//                            .pointsPossible(questionPoints)
//                            .pointsObtained(0)
//                            .isCorrect(false)
//                            .studentAnswer(null)
//                            .correctAnswer("N/A")
//                            .build());
//                    continue;
//                }
//
//                answered++;
//
//                // ✅ OBJECTIVE QUESTIONS
//                if (type.equalsIgnoreCase(VALUE_MULTIPLE_CHOICE) ||
//                        type.equalsIgnoreCase(VALUE_TRUE_FALSE) ||
//                        type.equalsIgnoreCase(VALUE_FILL_IN_THE_BANK)) {
//
//                    isScored = true;
//                    isCorrect = correctAnswer != null &&
//                            correctAnswer.equalsIgnoreCase(studentAnswer.trim());
//
//                    pointsEarned = isCorrect ? questionPoints : 0;
//                    correctAnswerDisplay = correctAnswer;
//                }
//
//                // 🤖 CODING QUESTIONS (AI GRADING)
//                else if (type.equalsIgnoreCase(VALUE_CODING) || type.equalsIgnoreCase(VALUE_WRITING)) {
//                    isScored = false;
//                    isFinal = false;
//                    pointsEarned = 0;
//                    isCorrect = false;
//                    correctAnswerDisplay = "Waiting for teacher to review";
//                }
//
//                logger.debug("isScore :: {}", isScored);
//
//                obtained += pointsEarned;
//                details.add(QuestionGradeDetail.builder()
//                        .questionId(qId)
//                        .questionType(type)
//                        .questionContent(question.getQuestionContent())
//                        .questionDifficulty(question.getDifficulty())
//                        .pointsPossible(questionPoints)
//                        .pointsObtained(pointsEarned)
//                        .isScore(isScored)
//                        .isCorrect(isCorrect)
//                        .studentAnswer(studentAnswer)
//                        .correctAnswer(correctAnswerDisplay)
//                        .build());
//            }
//
//            //8. Save Exam Result
//            ExamResultDomain examResultDomain = new ExamResultDomain();
//            examResultDomain.setExam(exam);
//            examResultDomain.setStudentId(studentId);
//            examResultDomain.setScore(obtained);
//
//            String detailsString = mapper.writeValueAsString(details);
//            examResultDomain.setDetails(detailsString);
//
//            String status = isFinal ? "GRADED" : "PENDING_REVIEW";
//            String summaryMessage = isFinal ? "Grading complete" : "Awaiting teacher review for subjective questions";
//            examResultDomain.setStatus(status);
//
//            examResultDomain.setTimeTaken(0);
//            examResultDomain.setGradedAt(LocalDateTime.now());
//
//            logger.info("Saving exam result domain for student {}: {}", studentId, examResultDomain);
//
//            examResultRepo.save(examResultDomain);
//            return GradingResult.builder()
//                    .answeredCount(answered)
//                    .obtainedScore(obtained)
//                    .totalPossibleScore(100)
//                    .totalQuestions(totalQuestions)
//                    .details(details)
//                    .summaryMessage(summaryMessage)
//                    .build();
//
//        } catch (Exception e) {
//            logger.error("Submit exam failed for exam: {}", exam.getId(), e);
//
//            return GradingResult.builder()
//                    .obtainedScore(0)
//                    .totalPossibleScore(100)
//                    .answeredCount(0)
//                    .totalQuestions(0)
//                    .summaryMessage("Grading failed – please contact support")
//                    .build();
//        }
//    }

    public GradingResult autoGradeExam(String progressJson, ExamDomain exam, String studentId) {
        List<QuestionGradeDetail> details = new ArrayList<>();
        double totalObtainedPoints = 0; // Use double for precise weighted math
        int answered = 0;
        boolean isFinal = true;

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ExamPaperQuestionResponse> submittedQuestions = mapper.readValue(
                    progressJson,
                    new TypeReference<List<ExamPaperQuestionResponse>>() {}
            );

            // 1. Pre-process: Count difficulty distribution and fetch questions
            List<Long> qIds = submittedQuestions.stream().map(ExamPaperQuestionResponse::getQuestionId).toList();
            Map<Long, QuestionBankDomain> questionMap = questionBankRepo.findAllById(qIds)
                    .stream().collect(Collectors.toMap(QuestionBankDomain::getId, q -> q));

            long easyCount = 0, medCount = 0, hardCount = 0;
            for (ExamPaperQuestionResponse sq : submittedQuestions) {
                QuestionBankDomain q = questionMap.get(sq.getQuestionId());
                if (q == null) continue;
                String diff = q.getDifficulty().toUpperCase();
                if (diff.contains("EASY")) easyCount++;
                else if (diff.contains("MEDIUM")) medCount++;
                else if (diff.contains("HARD")) hardCount++;
            }

            // 2. Calculate the base point unit (Value of 1 'Easy' question)
            // Ratio 1:2:4 (Easy:Medium:Hard)
            double weightedTotalUnits = (easyCount * 1.0) + (medCount * 2.0) + (hardCount * 4.0);
            double baseUnit = (weightedTotalUnits > 0)? (100.0 / weightedTotalUnits) : 0;

            // 3. Loop through and grade
            for (ExamPaperQuestionResponse submitted : submittedQuestions) {
                QuestionBankDomain question = questionMap.get(submitted.getQuestionId());
                if (question == null) continue;

                // Determine points for THIS question based on difficulty
                String diff = question.getDifficulty().toUpperCase();
                double questionWeight = diff.contains("HARD")? 4.0 : (diff.contains("MEDIUM")? 2.0 : 1.0);
                double potentialPoints = baseUnit * questionWeight;

                String type = question.getQuestionType();
                String studentAnswer = submitted.getStudentAnswer();
                boolean isCorrect = false;
                double pointsEarned = 0;
                String feedback = "";

                if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
                    feedback = "No answer provided.";
                } else {
                    answered++;
                    // ✅ OBJECTIVE GRADING
                    if (isObjective(type)) {
                        isCorrect = question.getCorrectAnswer().equalsIgnoreCase(studentAnswer.trim());
                        pointsEarned = isCorrect? potentialPoints : 0;
                        feedback = isCorrect? "Correct" : "Incorrect. Expected: " + question.getCorrectAnswer();
                    }
                    // 🤖 SUBJECTIVE (Requires AI or Manual Review)
                    else {
                        isFinal = false; // Cannot be final if coding/writing exists
                        pointsEarned = 0;
                        feedback = "Waiting for teacher to review";
                    }
                }
                totalObtainedPoints += pointsEarned;

                details.add(QuestionGradeDetail.builder()
                        .questionContent(question.getQuestionContent())
                        .questionId(question.getId())
                        .questionType(type)
                        .questionDifficulty(question.getDifficulty())
                        .pointsPossible((int) Math.round(potentialPoints))
                        .pointsObtained((int) Math.round(pointsEarned))
                        .isScoreEdit(false)
                        .isCorrect(isCorrect)
                        .studentAnswer(studentAnswer)
                        .correctAnswer(isObjective(type)? question.getCorrectAnswer() : "N/A")
                        .summaryMessage(feedback)
                        .build());
            }

            // 4. Finalize result
            int finalScore = (int) Math.round(totalObtainedPoints);
            String status = isFinal? "GRADED" : "PENDING_REVIEW";

            String grade = getEvaluateGrade(status, finalScore);

            Optional<ExamResultDomain> existExamResult = examResultRepo.findExamResultDomainByStudentIdAndExam_Id(studentId, exam.getId());

            ExamResultDomain resultDomain = existExamResult.orElseGet(ExamResultDomain::new);

            LocalDateTime now = LocalDateTime.now();
            long timeTakenMillis = Duration.between(exam.getExamDate(), now).toMillis();
            resultDomain.setExam(exam);
            resultDomain.setGrade(grade);
            resultDomain.setTimeTaken(timeTakenMillis);
            resultDomain.setStudentId(studentId);
            resultDomain.setScore(finalScore);
            resultDomain.setStatus(status);
            resultDomain.setDetails(mapper.writeValueAsString(details));
            resultDomain.setGradedAt(LocalDateTime.now());
            examResultRepo.save(resultDomain);

            return GradingResult.builder()
                    .obtainedScore(finalScore)
                    .totalPossibleScore(100) // Always normalized to 100%
                    .answeredCount(answered)
                    .totalQuestions(submittedQuestions.size())
                    .details(details)
                    .summaryMessage(isFinal? "Grading complete" : "Awaiting teacher review for coding/writing tasks")
                    .build();

        } catch (Exception e) {
            logger.error("Auto-grading failed for student {}: {}", studentId, e.getMessage());
            return GradingResult.builder().summaryMessage("System error during grading").build();
        }
    }

    private static String getEvaluateGrade(String status, int finalScore) {
        String grade = "";
        if (status.equalsIgnoreCase("GRADED")) {
            if (finalScore >= 90) {
                grade = "A"; // Excellent / ល្អប្រសើរ
            } else if (finalScore >= 80) {
                grade = "B"; // Very Good / ល្អណាស់
            } else if (finalScore >= 70) {
                grade = "C"; // Good / ល្អ
            } else if (finalScore >= 60) {
                grade = "D"; // Satisfactory / មធ្យម
            } else if (finalScore >= 50) {
                grade = "E"; // Pass Threshold / ខ្សោយ
            } else {
                grade = "F"; // Fail / ធ្លាក់
            }
        } else {
            grade = "PENDING";
        }
        return grade;
    }

    private boolean isObjective(String type) {
        return type.equalsIgnoreCase("MULTIPLE_CHOICE") ||
        type.equalsIgnoreCase("TRUE_FALSE") ||
        type.equalsIgnoreCase("FILL_IN_THE_BLANK");
    }

    public Map<String, Object> getSingleExamInfoDetail(long examId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.info("Start - getSingleExamInfoDetail with exam ID :: {}", examId);
            Optional<ExamDomain> examInfoOptional = examRepo.findById(examId);

            if(examInfoOptional.isEmpty()){
                logger.error("Exam info not found with ID :: {}", examId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Exam info not found", "");
                return finalServiceResponse;
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", examInfoOptional.get());
            logger.debug("End - getSingleExamInfoDetail with response :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE,"getSingleExamInfoDetail", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Integer> calculatePointDistribution(int totalPoints, int easyCount, int medCount, int hardCount) {
        logger.info("Start - calculatePointDistribution with totalPoints: {}, easyCount: {}, medCount: {}, hardCount: {}",
                totalPoints, easyCount, medCount, hardCount);
        // 1. Define Ratios
        double weightE = 1.0;
        double weightM = 2.0;
        double weightH = 4.0;

        // 2. Calculate Weighted Total
        double weightedSum = (easyCount * weightE) + (medCount * weightM) + (hardCount * weightH);
        double pointPerUnit = totalPoints / weightedSum;

        // 3. Calculate "Ideal" points (with decimals)
        double idealE = weightE * pointPerUnit;
        double idealM = weightM * pointPerUnit;
        double idealH = weightH * pointPerUnit;

        // 4. Initial integer allocation (Round Down)
        int scoreE = (int) Math.floor(idealE);
        int scoreM = (int) Math.floor(idealM);
        int scoreH = (int) Math.floor(idealH);

        // 5. Calculate current total and remainder
        int currentTotal = (scoreE * easyCount) + (scoreM * medCount) + (scoreH * hardCount);
        int remainingPoints = totalPoints - currentTotal;

        // 6. Largest Remainder Distribution
        // In a real classroom, we prioritize adding remainder points to Hard, then Medium
        while (remainingPoints > 0) {
            if (hardCount > 0 && remainingPoints >= hardCount) {
                scoreH++;
                remainingPoints -= hardCount;
            } else if (medCount > 0 && remainingPoints >= medCount) {
                scoreM++;
                remainingPoints -= medCount;
            } else if (easyCount > 0 && remainingPoints >= easyCount) {
                scoreE++;
                remainingPoints -= easyCount;
            } else {
                // If remainder is too small to split evenly across a group,
                // assign to the first 'n' questions in the hardest category
                break;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("easyPoints", scoreE);
        result.put("mediumPoints", scoreM);
        result.put("hardPoints", scoreH);
        result.put("finalTotal", (scoreE * easyCount) + (scoreM * medCount) + (scoreH * hardCount));

        return result;
    }
}
