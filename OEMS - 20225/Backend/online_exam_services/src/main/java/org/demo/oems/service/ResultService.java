package org.demo.oems.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ExamDomain;
import org.demo.oems.domain.ExamResultDomain;
import org.demo.oems.domain.UserInfoDomain;
import org.demo.oems.payload.response.ExamResultsResponse;
import org.demo.oems.payload.response.GradingDetailsResponse;
import org.demo.oems.payload.response.QuestionGradeDetail;
import org.demo.oems.repository.ExamResultRepo;
import org.demo.oems.repository.UserInfoRepo;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class ResultService {

    private static final Logger logger = LogManager.getLogger(ResultService.class);

    private final ExamResultRepo examResultRepo;

    private final ExamService examService;
    private final UserInfoRepo userInfoRepo;

    public ExamResultDomain findExamResultById(long resultId){
        Optional<ExamResultDomain> examResultRepoOptional = examResultRepo.findById(resultId);
        return examResultRepoOptional.orElse(null);
    }

    public ExamResultDomain findExamResultByStudentIdAndExamId(String studentId, long examId){
        Optional<ExamResultDomain> examResultRepoOptional = examResultRepo.findExamResultDomainByStudentIdAndExam_Id(studentId, examId);
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

    public void saveExamResult(ExamResultDomain examResultDomain){
        examResultRepo.save(examResultDomain);
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

    public ExamResultsResponse formExamResultResponse(ExamResultDomain examResultDomain){
        ExamResultsResponse examResultsResponse = new ExamResultsResponse();
        examResultsResponse.setId(examResultDomain.getId());
        examResultsResponse.setExamId(String.valueOf(examResultDomain.getExam().getId()));
        examResultsResponse.setExamName(examResultDomain.getExam().getExamTitle());
        examResultsResponse.setClassId(String.valueOf(examResultDomain.getExam().getClassId()));
        examResultsResponse.setStudentId(examResultDomain.getStudentId());
        examResultsResponse.setScore(examResultDomain.getScore());
        examResultsResponse.setStatus(examResultDomain.getStatus());
        examResultsResponse.setTimeTaken(examResultDomain.getTimeTaken());
        examResultsResponse.setGradedAt(examResultDomain.getGradedAt());
        examResultsResponse.setGrade(examResultDomain.getGrade());
        examResultsResponse.setSubjectId(examResultDomain.getExam().getSubjectId());

        Optional<UserInfoDomain> userInfoDomainOptional = userInfoRepo.findUserInfoDomainByUserId(examResultDomain.getStudentId());
        String studentName = userInfoDomainOptional.isEmpty() ? "" : userInfoDomainOptional.get().getName();

        examResultsResponse.setStudentName(studentName);
        return examResultsResponse;
    }

    public GradingDetailsResponse formGradingDetailsResponse(ExamResultDomain examResultDomain){
        GradingDetailsResponse gradingDetailsResponse = new GradingDetailsResponse();
        gradingDetailsResponse.setId(examResultDomain.getId());
        gradingDetailsResponse.setExamId(String.valueOf(examResultDomain.getExam().getId()));
        gradingDetailsResponse.setExamName(examResultDomain.getExam().getExamTitle());
        gradingDetailsResponse.setClassId(String.valueOf(examResultDomain.getExam().getClassId()));
        gradingDetailsResponse.setStudentId(examResultDomain.getStudentId());
        gradingDetailsResponse.setScore(examResultDomain.getScore());
        gradingDetailsResponse.setStatus(examResultDomain.getStatus());
        gradingDetailsResponse.setTimeTaken(examResultDomain.getTimeTaken());
        gradingDetailsResponse.setGradedAt(examResultDomain.getGradedAt());
        gradingDetailsResponse.setGrade(examResultDomain.getGrade());
        // Assuming details is a JSON string, you might want to convert it to a Map
        // For simplicity, we'll just put the raw string here

        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.debug("Exam Result Domain details JSON :: {}details JSON :: {}", examResultDomain.getDetails());
            // 1. Get the raw string from your DB
            String rawDbJson = examResultDomain.getDetails();

            if (rawDbJson!= null &&!rawDbJson.isEmpty()) {
                // 2. DESERIALIZE the string into a List of Objects
                // This prevents double escaping
                List<QuestionGradeDetail> detailsList = mapper.readValue(rawDbJson, new TypeReference<>() {});

                logger.debug("details list after deserialization :: {}", detailsList);
                // 3. Set the list DIRECTLY to the DTO to avoid the "details.details" nesting
                gradingDetailsResponse.setDetails(detailsList);
            }

        } catch (JsonProcessingException e) {
            logger.error("JSON Deserialization failed for result ID {}: {}", examResultDomain.getId(), e.getMessage());
            gradingDetailsResponse.setDetails(new ArrayList<>()); // Fallback to empty list
        }

        return gradingDetailsResponse;
    }

    public Map<String, Object> getClassesResultByClassId(long classId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        List<ExamResultsResponse> examResultsLists = new ArrayList<>();
        try{
            logger.debug("Start - getClassesResultByClassId :: {}", classId);
            List<ExamDomain> completedExamLists = examService.findAllCompletedExamLists(classId);

            for(ExamDomain examDomain : completedExamLists){
                long examId = examDomain.getId();

                List<ExamResultDomain> resultDomain = findAllExamResultByExamId(examId);
                for(ExamResultDomain examResultDomain : resultDomain){
                    ExamResultsResponse examResults = formExamResultResponse(examResultDomain);
                    examResultsLists.add(examResults);
                }

            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", examResultsLists);

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
        List<ExamResultsResponse> examResultsLists = new ArrayList<>();
        try{
            logger.debug("Start - getResultsByStudentId :: {}", studentId);
            List<ExamResultDomain> completedExamLists = findAllExamResultsByStudentId(studentId);

            for(ExamResultDomain examResultDomain : completedExamLists){
                ExamResultsResponse examResults = formExamResultResponse(examResultDomain);
                examResultsLists.add(examResults);
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", examResultsLists);
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
        List<ExamResultsResponse> examResultsResponse = new ArrayList<>();
        try{
            logger.debug("Start - getClassesResultByExamId :: {}", examId);

            List<ExamResultDomain> resultLists = findAllExamResultByExamId(examId);

            for(ExamResultDomain examResultDomain : resultLists){
                logger.debug("Exam Result Domain :: {}", examResultDomain);
                examResultsResponse.add(formExamResultResponse(examResultDomain));
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", examResultsResponse);
            logger.debug("End - getClassesResultByExamId :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception - getClassesResultByExamId :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getGradingResultByExamIdAndStudentId(long examId, String studentId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Start - getGradingResultByExamIdAndStudentId :: examId: {}, studentId: {}", examId, studentId);

            ExamResultDomain examResultDomain = findExamResultByStudentIdAndExamId(studentId, examId);
            GradingDetailsResponse gradingDetailsResponse = formGradingDetailsResponse(examResultDomain);

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", gradingDetailsResponse);
            logger.debug("End - getGradingResultByExamIdAndStudentId :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception - getGradingResultByExamIdAndStudentId :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    @Transactional // Ensures the connection stays open for LOB/TEXT streaming
    public Map<String, Object> gradingStudent(GradingDetailsResponse gradingDetails) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try {
            logger.info("Teacher is updating marks for Result ID: {}", gradingDetails.getId());

            Optional<ExamResultDomain> resultOpt = examResultRepo.findById(gradingDetails.getId());
            if (resultOpt.isEmpty()) {
                return ResponseUtils.formatAPIResponse("400", "Exam result not found", "");
            }

            ExamResultDomain examResult = resultOpt.get();
            ObjectMapper mapper = new ObjectMapper();

            double totalSum = 0;
            boolean stillHasPending = false;

            // 1. Iterate and Re-calculate the sum of all points
            for (QuestionGradeDetail detail : gradingDetails.getDetails()) {
                // Add the points obtained for THIS question to the total sum
                totalSum += detail.getPointsObtained();

                // 2. Logic to check for pending subjective tasks
                // If any coding/writing task hasn't been confirmed yet, keep status as PENDING_REVIEW
                if (isSubjective(detail.getQuestionType()) &&!detail.isScoreEdit()) {
                    stillHasPending = true;
                }
            }

            // 3. Update the JSON snapshot in the database
            // This ensures the "details" view in your dashboard shows the teacher's edits
            // Calling writeValueAsString here avoids double-escaping
            String updatedDetailsJson = mapper.writeValueAsString(gradingDetails.getDetails());
            examResult.setDetails(updatedDetailsJson);

            // 4. Update the aggregate final score (0-100%)
            int finalScore = (int) Math.round(totalSum);
            examResult.setScore(finalScore);

            // 5. Finalize status and timestamp
            String status = stillHasPending? "PENDING_REVIEW" : "GRADED";
            examResult.setStatus(status);
            examResult.setGradedAt(LocalDateTime.now());

            if(status.equalsIgnoreCase("GRADED")){
                // 6. Optional: Auto-assign letter grade based on final score
                if (finalScore >= 90) {
                    examResult.setGrade("A");
                } else if (finalScore >= 80) {
                    examResult.setGrade("B");
                } else if (finalScore >= 70) {
                    examResult.setGrade("C");
                } else if (finalScore >= 60) {
                    examResult.setGrade("D");
                } else {
                    examResult.setGrade("F");
                }
            }

            examResultRepo.save(examResult);

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Grading updated successfully", examResult);

            logger.info("Grading complete for student {}. Final Score: {}, Status: {}", examResult.getStudentId(), finalScore, status);

            return finalServiceResponse;

        } catch (Exception e) {
            logger.error("Exception in gradingStudent :: {}", e.getMessage(), e);
            return ResponseUtils.formatAPIResponse("500", "Internal grading error", "");
        }
    }

    private boolean isSubjective(String type) {
        return "CODING".equalsIgnoreCase(type) || "WRITING".equalsIgnoreCase(type);
    }

    public Map<String, Object> getGradingDetailsByStudentIdAndExamId(String studentId, long examId) {
            Map<String, Object> finalServiceResponse = new HashMap<>();
            try{
                logger.debug("Start - getGradingDetailsByStudentIdAndExamId :: studentId: {}, examId: {}", studentId);
                ExamResultDomain examResultDomain = findExamResultByStudentIdAndExamId(studentId, examId);

                GradingDetailsResponse gradingDetailsResponse = formGradingDetailsResponse(examResultDomain);

                finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", gradingDetailsResponse);
                logger.debug("End - getGradingDetailsByStudentIdAndExamId :: {}", finalServiceResponse);
                return finalServiceResponse;
            }catch (Exception e){
                logger.error("Exception - getGradingDetailsByStudentIdAndExamId :: {}", e.getMessage());
                finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
                return finalServiceResponse;
            }
    }
}
