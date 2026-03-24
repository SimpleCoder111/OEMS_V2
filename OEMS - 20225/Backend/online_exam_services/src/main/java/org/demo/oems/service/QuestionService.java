package org.demo.oems.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ChapterDomain;
import org.demo.oems.domain.ClassDomain;
import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.QuestionBankInsertRequest;
import org.demo.oems.payload.response.QuestionDetailsResponse;
import org.demo.oems.payload.response.QuestionSummaryResponse;
import org.demo.oems.repository.ClassRepo;
import org.demo.oems.repository.QuestionBankRepo;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.util.*;

import static org.demo.oems.utils.CommonConstantUtils.*;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private static final Logger logger = LogManager.getLogger(QuestionService.class);

    private final SubjectService subjectService;

    private static final HashSet<String> hashSet = new HashSet<>();

    private final QuestionBankRepo questionBankRepo;

    private final ClassRepo classRepo;

    static {
        hashSet.add(VALUE_MULTIPLE_CHOICE);
        hashSet.add(VALUE_TRUE_FALSE);
        hashSet.add(VALUE_FILL_IN_THE_BANK);
        hashSet.add(VALUE_CODING);
        hashSet.add(VALUE_WRITING);
    }

    public boolean validQuestionType(String questionType) {
        return hashSet.contains(questionType.trim());
    }

    public List<QuestionBankDomain> getAllQuestionsBySubjectId(long subjectId) {
        return questionBankRepo.findQuestionBankDomainsBySubject_IdOrderByChapter_IdAsc(subjectId);
    }

    public List<QuestionBankDomain> getAllQuestions() {
        return questionBankRepo.findAll();
    }

    public void deleteQuestionById(long questionId) {
        questionBankRepo.deleteById(questionId);
    }

    public QuestionBankDomain getQuestionDetailsById(long questionId) {
        Optional<QuestionBankDomain> questionBankDomainOptional = questionBankRepo.findById(questionId);
        return questionBankDomainOptional.orElse(null);
    }

    public QuestionDetailsResponse formQuestionResponse(QuestionBankDomain questionBankDomain) throws JsonProcessingException {
        QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
        questionDetailsResponse.setId(questionBankDomain.getId());
        questionDetailsResponse.setQuestionType(questionBankDomain.getQuestionType());
        questionDetailsResponse.setQuestionContent(questionBankDomain.getQuestionContent());

        ObjectMapper mapper = new ObjectMapper();
        List<String> optionLists = mapper.readValue(
                questionBankDomain.getOptionContent(),
                new TypeReference<List<String>>() {}
        );

        questionDetailsResponse.setChapterOrder(questionBankDomain.getChapter().getChapterIndex());
        questionDetailsResponse.setChapterId(questionBankDomain.getChapter().getId());
        questionDetailsResponse.setChapterName(questionBankDomain.getChapter().getChapter());
        questionDetailsResponse.setOptionContent(optionLists);
        questionDetailsResponse.setCorrectAnswer(questionBankDomain.getCorrectAnswer());
        questionDetailsResponse.setDifficulty(questionBankDomain.getDifficulty());
        questionDetailsResponse.setCreatedBy(questionBankDomain.getCreatedBy());
        questionDetailsResponse.setPoints(String.valueOf(questionBankDomain.getPoints()));
        questionDetailsResponse.setCreatedAt(String.valueOf(questionBankDomain.getCreatedAt()));
        return questionDetailsResponse;
    }

    public List<QuestionDetailsResponse> formQuestionResponseList(List<QuestionBankDomain> questionBankDomains) throws JsonProcessingException {
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();
        for(QuestionBankDomain questionBankDomain: questionBankDomains){
            questionDetailsResponses.add(formQuestionResponse(questionBankDomain));
        }
        return questionDetailsResponses;
    }

    public Map<String, Object> addQuestionToSubject(long subjectId, List<QuestionBankInsertRequest> requestPayload) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try {
            logger.info("Start - addQuestionToSubject service :: subjectId - {}, questionBankInsertRequest - {}", subjectId, requestPayload);

            SubjectDomain subjectDomain = subjectService.getSubjectInfoById(requestPayload.get(0).getSubjectId());
            if (subjectDomain == null) {
                logger.error("Subject not found with id: {}", requestPayload.get(0).getSubjectId());
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Subject not found with id: " + requestPayload.get(0).getSubjectId(), "");
                return finalServiceResponse;
            }

            ChapterDomain chapterDomain = subjectService.getChapterInfoById(requestPayload.get(0).getChapterId());
            if (chapterDomain == null) {
                logger.error("Chapter not found with id: {}", requestPayload.get(0).getChapterId());
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Chapter not found with id: " + requestPayload.get(0).getChapterId(), "");
                return finalServiceResponse;
            }

            int count = 0;
            for(QuestionBankInsertRequest questionBankInsertRequest: requestPayload) {

                boolean isQuestionTypeValid = validQuestionType(questionBankInsertRequest.getQuestionType());
                if (!isQuestionTypeValid) {
                    logger.error("Invalid question type: {}", questionBankInsertRequest.getQuestionType());
                    continue;
                }

                logger.info("Creating QuestionBankDomain entity with provided details");
                QuestionBankDomain questionBankDomain = new QuestionBankDomain();
                questionBankDomain.setSubject(subjectDomain);
                questionBankDomain.setChapter(chapterDomain);
                questionBankDomain.setQuestionType(questionBankInsertRequest.getQuestionType().toUpperCase());
                questionBankDomain.setQuestionContent(questionBankInsertRequest.getQuestionContent());

                ObjectMapper mapper = new ObjectMapper();
                String optionListJsonString = mapper.writeValueAsString(questionBankInsertRequest.getOptionLists());

                questionBankDomain.setOptionContent(optionListJsonString);
                questionBankDomain.setCorrectAnswer(questionBankInsertRequest.getCorrectAnswer());
                questionBankDomain.setDifficulty(questionBankInsertRequest.getDifficulty());
                questionBankDomain.setCreatedAt(LocalDateTime.now());
                questionBankDomain.setCreatedBy(questionBankInsertRequest.getCreatedBy());

                int points = questionBankInsertRequest.getScore() == 0 ? 1 : questionBankInsertRequest.getScore();
                questionBankDomain.setPoints(points);
                questionBankRepo.save(questionBankDomain);
                count ++;
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Successfully added "
                    + count + " questions to subject id " + requestPayload.get(0).getSubjectId(),  "");
            logger.info("End - addQuestionToSubject service :: {}", finalServiceResponse);
            return finalServiceResponse;
        } catch (Exception e) {
            logger.error("Exception - addQuestionToSubject service :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getAllQuestionsBySubject(long subjectId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try {
            logger.info("Start - getAllQuestionsBySubject service :: subjectId - {}", subjectId);
            List<QuestionBankDomain> questionsLists = this.getAllQuestionsBySubjectId(subjectId);

            List<QuestionDetailsResponse> questionDetailsResponseList = this.formQuestionResponseList(questionsLists);

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Questions retrieved successfully for subject id: " + subjectId, questionDetailsResponseList);
            logger.info("End - getAllQuestionsBySubject service :: {}", finalServiceResponse);
            return finalServiceResponse;
        } catch (Exception e) {
            logger.error("Exception - getAllQuestionsBySubject service :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> editQuestionByQuestionId(long questionId, QuestionBankInsertRequest requestPayload) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try {
            logger.info("Start - editQuestionByQuestionId service :: questionId - {}, questionBankInsertRequest - {}", questionId, requestPayload);

            QuestionBankDomain questionBankDomain = this.getQuestionDetailsById(questionId);
            if (questionBankDomain == null) {
                logger.error("Question not found with id: {}", questionId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Question not found with id: " + questionId, "");
                return finalServiceResponse;
            }

            boolean isQuestionTypeValid = validQuestionType(requestPayload.getQuestionType());
            if (!isQuestionTypeValid) {
                logger.error("Invalid question type: {}", requestPayload.getQuestionType());
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Invalid question type: " + requestPayload.getQuestionType(), "");
                return finalServiceResponse;
            }

            SubjectDomain subjectDomain = subjectService.getSubjectInfoById(requestPayload.getSubjectId());
            if (subjectDomain == null) {
                logger.error("Subject not found with id: {}", requestPayload.getSubjectId());
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Subject not found with id: " + requestPayload.getSubjectId(), "");
                return finalServiceResponse;
            }

            ChapterDomain chapterDomain = subjectService.getChapterInfoById(requestPayload.getChapterId());
            if (chapterDomain == null) {
                logger.error("Chapter not found with id: {}", requestPayload.getChapterId());
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Chapter not found with id: " + requestPayload.getChapterId(), "");
                return finalServiceResponse;
            }

            logger.info("Creating QuestionBankDomain entity with provided details");
            questionBankDomain.setSubject(subjectDomain);
            questionBankDomain.setChapter(chapterDomain);
            questionBankDomain.setQuestionType(requestPayload.getQuestionType().toUpperCase());
            questionBankDomain.setQuestionContent(requestPayload.getQuestionContent());
            questionBankDomain.setOptionContent(requestPayload.getOptionLists().toString());
            questionBankDomain.setCorrectAnswer(requestPayload.getCorrectAnswer());
            questionBankDomain.setDifficulty(requestPayload.getDifficulty());
            questionBankDomain.setCreatedAt(LocalDateTime.now());
            questionBankDomain.setCreatedBy(requestPayload.getCreatedBy());

            int points = requestPayload.getScore() == 0 ? 1 : requestPayload.getScore();
            questionBankDomain.setPoints(points);
            questionBankRepo.save(questionBankDomain);

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Question added successfully to subject", questionBankDomain);
            logger.info("End - addQuestionToSubject service :: {}", finalServiceResponse);
            return finalServiceResponse;
        } catch (Exception e) {
            logger.error("Exception - addQuestionToSubject service :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> deleteQuestionById(Long questionId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.info("Start - deleteQuestionById service :: questionId - {}", questionId);
            questionBankRepo.deleteById(questionId);

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Question deleted successfully with id: " + questionId, "");
            logger.info("End - deleteQuestionById service :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception - deleteQuestionById service :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getOverallQuestionSummaryDashboard() {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try {
            logger.info("Start - getOverallQuestionSummaryDashboard service");

            long countEasyQuestions = questionBankRepo.countQuestionBankDomainsByDifficulty(VALUE_EASY);
            long countMediumQuestions = questionBankRepo.countQuestionBankDomainsByDifficulty(VALUE_MEDIUM);
            long countHardQuestions = questionBankRepo.countQuestionBankDomainsByDifficulty(VALUE_HARD);
            long totalQuestions = questionBankRepo.count();
            long totalQCMQuestions = questionBankRepo.countQuestionBankDomainsByQuestionTypeEqualsIgnoreCase(VALUE_MULTIPLE_CHOICE);
            long totalFillBlankQuestions = questionBankRepo.countQuestionBankDomainsByQuestionTypeEqualsIgnoreCase(VALUE_FILL_IN_THE_BANK);
            long totalTrueFalseQuestions = questionBankRepo.countQuestionBankDomainsByQuestionTypeEqualsIgnoreCase(VALUE_TRUE_FALSE);
            long totalCodingQuestions = questionBankRepo.countQuestionBankDomainsByQuestionTypeEqualsIgnoreCase(VALUE_CODING);
            long totalWritingQuestions = questionBankRepo.countQuestionBankDomainsByQuestionTypeEqualsIgnoreCase(VALUE_WRITING);

            QuestionSummaryResponse questionSummaryResponse = new QuestionSummaryResponse();
            questionSummaryResponse.setTotalCodingQuestions(totalCodingQuestions);;
            questionSummaryResponse.setTotalFillBlankQuestions(totalFillBlankQuestions);
            questionSummaryResponse.setTotalWritingQuestions(totalWritingQuestions);
            questionSummaryResponse.setTotalTrueFalseQuestions(totalTrueFalseQuestions);
            questionSummaryResponse.setTotalQcmQuestions(totalQCMQuestions);
            questionSummaryResponse.setTotalQuestions(totalQuestions);
            questionSummaryResponse.setTotalMediumQuestions(countMediumQuestions);
            questionSummaryResponse.setTotalHardQuestions(countHardQuestions);
            questionSummaryResponse.setTotalEasyQuestions(countEasyQuestions);

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Overall question summary dashboard retrieved successfully", questionSummaryResponse);
            logger.info("End - getOverallQuestionSummaryDashboard service :: {}", finalServiceResponse);
            return finalServiceResponse;
        } catch (Exception e) {
            logger.error("Exception - getOverallQuestionSummaryDashboard service :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getQuestionSummaryDashboardByTeacherId(String teacherId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try {
            logger.info("Start - getOverallQuestionSummaryDashboard service");

            List<ClassDomain> classLists = classRepo.getClassDomainsByTeacherIdEqualsIgnoreCase(teacherId);
            long totalQuestions = 0;
            long totalEasyQuestions = 0;
            long totalMediumQuestions = 0;
            long totalHardQuestions = 0;
            long totalQcmQuestions = 0;
            long totalFillBlankQuestions = 0;
            long totalTrueFalseQuestions = 0;
            long totalCodingQuestions = 0;
            long totalWritingQuestions = 0;

            for(ClassDomain classDomain: classLists){
                long subjectId = classDomain.getSubjectId();

                long countEasyQuestions = questionBankRepo.countBySubject_IdAndDifficulty(subjectId, VALUE_EASY);
                long countMediumQuestions = questionBankRepo.countBySubject_IdAndDifficulty(subjectId, VALUE_MEDIUM);
                long countHardQuestions = questionBankRepo.countBySubject_IdAndDifficulty(subjectId, VALUE_HARD);
                long countTotalQuestions = questionBankRepo.countAllBySubject_Id(subjectId);
                long countQCMQuestions = questionBankRepo.countBySubject_IdAndQuestionType(subjectId, VALUE_MULTIPLE_CHOICE);
                long countFillBlankQuestions = questionBankRepo.countBySubject_IdAndQuestionType(subjectId, VALUE_FILL_IN_THE_BANK);
                long countTrueFalseQuestions = questionBankRepo.countBySubject_IdAndQuestionType(subjectId, VALUE_TRUE_FALSE);
                long countCodingQuestions = questionBankRepo.countBySubject_IdAndQuestionType(subjectId, VALUE_CODING);
                long countWritingQuestions = questionBankRepo.countBySubject_IdAndQuestionType(subjectId, VALUE_WRITING);

                totalQuestions += countTotalQuestions;
                totalEasyQuestions += countEasyQuestions;
                totalMediumQuestions += countMediumQuestions;
                totalHardQuestions += countHardQuestions;
                totalQcmQuestions += countQCMQuestions;
                totalFillBlankQuestions += countFillBlankQuestions;
                totalTrueFalseQuestions += countTrueFalseQuestions;
                totalCodingQuestions += countCodingQuestions;
                totalWritingQuestions += countWritingQuestions;

            }

            QuestionSummaryResponse questionSummaryResponse = new QuestionSummaryResponse();
            questionSummaryResponse.setTotalCodingQuestions(totalCodingQuestions);;
            questionSummaryResponse.setTotalFillBlankQuestions(totalFillBlankQuestions);
            questionSummaryResponse.setTotalWritingQuestions(totalWritingQuestions);
            questionSummaryResponse.setTotalTrueFalseQuestions(totalTrueFalseQuestions);
            questionSummaryResponse.setTotalQcmQuestions(totalQcmQuestions);
            questionSummaryResponse.setTotalQuestions(totalQuestions);
            questionSummaryResponse.setTotalMediumQuestions(totalMediumQuestions);
            questionSummaryResponse.setTotalHardQuestions(totalHardQuestions);
            questionSummaryResponse.setTotalEasyQuestions(totalEasyQuestions);

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Overall question summary dashboard retrieved successfully", questionSummaryResponse);
            logger.info("End - getOverallQuestionSummaryDashboard service :: {}", finalServiceResponse);
            return finalServiceResponse;
        } catch (Exception e) {
            logger.error("Exception - getOverallQuestionSummaryDashboard service :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }

    }

    public Map<String, Object> getAllQuestionsByTeacherID(String teacherId) {

        Map<String, Object> finalServiceResponse = new HashMap<>();
        try {
            logger.info("Start - getAllQuestionsByTeacherID service :: teacherId - {}", teacherId);

            List<ClassDomain> classLists = classRepo.getClassDomainsByTeacherIdEqualsIgnoreCase(teacherId);
            List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<>();

            for(ClassDomain classDomain: classLists){
                long subjectId = classDomain.getSubjectId();
                List<QuestionBankDomain> questionBankDomains = this.getAllQuestionsBySubjectId(subjectId);
                questionDetailsResponseList.addAll(this.formQuestionResponseList(questionBankDomains));
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Questions retrieved successfully for teacher id: " + teacherId, questionDetailsResponseList);
            logger.info("End - getAllQuestionsByTeacherID service :: {}", finalServiceResponse);
            return finalServiceResponse;
        } catch (Exception e) {
            logger.error("Exception - getAllQuestionsByTeacherID service :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }
}
