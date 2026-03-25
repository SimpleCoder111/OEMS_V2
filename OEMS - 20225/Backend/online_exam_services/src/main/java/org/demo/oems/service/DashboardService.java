package org.demo.oems.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.demo.oems.domain.*;
import org.demo.oems.payload.response.*;
import org.demo.oems.repository.*;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.demo.oems.utils.CommonConstantUtils.*;


@Service
@RequiredArgsConstructor
public class DashboardService {
    private final Logger logger = LogManager.getLogger(DashboardService.class);

    private final SubjectRepo subjectRepository;

    private final ExamRepo examRepository;

    private final ExamResultRepo examResultRepository;

    private final UserInfoRepo userRepository;

    private final RoleRepo roleRepo;

    private final ClassroomRepo classroomRepo;

    private final ClassRepo classRepo;

    private final ActivityLogService activityLogService;

    public DashboardStatisticResponse getDashboardStatistic() {
        try {
            // Current month boundaries (using LocalDateTime for precision)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfCurrentMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
            LocalDateTime endOfPreviousMonth = startOfCurrentMonth.minusNanos(1);

            // 1. Subject statistics (assuming all subjects are "active" or you have a status field)
            long activeSubjectCount = subjectRepository.count();  // or countByStatus("active") if you have status
            long previousSubjectCount = subjectRepository.countByCreatedAtBefore(startOfCurrentMonth);  // Adjust logic if needed
            String subjectChange = calculatePercentageChange(previousSubjectCount, activeSubjectCount);

            logger.debug("Subject previous month :: {}, Subject this month :: {}, Subject Change :: {}",previousSubjectCount, activeSubjectCount, subjectChange);

            // 2. Exam statistics (exams created this month)
            long examThisMonth = examRepository.countByExamDateBetween(startOfCurrentMonth, now);
            long examPreviousMonth = examRepository.countByExamDateBetween(startOfPreviousMonth, endOfPreviousMonth);
            String examChange = calculatePercentageChange(examPreviousMonth, examThisMonth);
            logger.debug("Exam previous month :: {}, Exam this month :: {}, Exam Change :: {}",examPreviousMonth, examThisMonth, examChange);

            // 3. User statistics (by role)
            long totalStudent = userRepository.countByRole_RoleNameIgnoreCase(VALUE_STUDENT);
            logger.debug("Trying to get total student by role name :: {}", totalStudent);

            long totalTeacher = userRepository.countByRole_RoleNameIgnoreCase(VALUE_TEACHER);
            logger.debug("Trying to get total student by role name :: {}", totalTeacher);

            // Student/Teacher changes (users registered this month vs previous)
            long studentThisMonth = userRepository.countByRole_RoleNameIgnoreCaseAndCreatedAtBetween(VALUE_STUDENT, startOfCurrentMonth, now);
            long studentPreviousMonth = userRepository.countByRole_RoleNameIgnoreCaseAndCreatedAtBetween(VALUE_STUDENT, startOfPreviousMonth, endOfPreviousMonth);
            String studentChange = calculatePercentageChange(studentPreviousMonth, studentThisMonth);
            logger.debug("Student previous month :: {}, Student this month :: {}, Student Change :: {}",studentPreviousMonth, studentThisMonth, studentChange);

            long teacherThisMonth = userRepository.countByRole_RoleNameIgnoreCaseAndCreatedAtBetween(VALUE_TEACHER, startOfCurrentMonth, now);
            long teacherPreviousMonth = userRepository.countByRole_RoleNameIgnoreCaseAndCreatedAtBetween(VALUE_TEACHER, startOfPreviousMonth, endOfPreviousMonth);
            String teacherChange = calculatePercentageChange(teacherPreviousMonth, teacherThisMonth);
            logger.debug("Teacher previous month :: {}, Teacher this month :: {}, Teacher Change :: {}",teacherPreviousMonth, teacherThisMonth, teacherChange);

            return DashboardStatisticResponse.builder()
                    .activeSubject((int) activeSubjectCount)
                    .subjectChange(subjectChange)
                    .examThisMonth((int) examThisMonth)
                    .examChange(examChange)
                    .totalStudent((int) totalStudent)
                    .totalTeacher((int) totalTeacher)
                    .studentChange(studentChange)
                    .teacherChange(teacherChange)
                    .build();
        }catch (Exception e){
            logger.error(LOG_PREFIX_EXCEPTION_IN_SERVICE, "Get Dashboard Statistic", e.getMessage());
            return DashboardStatisticResponse.builder().build();
        }
    }

    private String calculatePercentageChange(long previous, long current) {
        if (previous == 0) {
            return current > 0 ? "+" + current * 100 + "%" : "0%";
        }
        double change = ((double) (current - previous) / previous) * 100;
        String sign = change > 0 ? "+" : "";
        return sign + String.format("%.0f%%", change);
    }

    public List<GradeDistributionResponse> getOverallGradeDistribution() {
        // 1. Fetch aggregated data from DB
        List<GradeCountProjection> results = examResultRepository.findOverallGradeDistribution();

        // 2. Map results to a Map for easy lookup
        Map<String, Long> gradeMap = results.stream()
                .collect(Collectors.toMap(GradeCountProjection::getGrade, GradeCountProjection::getCount));

        // 3. Calculate total for percentage math
        long totalCount = results.stream().mapToLong(GradeCountProjection::getCount).sum();

        // 4. Build response using your predefined scale (A-F)
        List<String> gradeLevels = List.of("A", "B", "C", "D", "E", "F");
        List<GradeDistributionResponse> serviceResponse = new ArrayList<>();

        for (String level : gradeLevels) {
            long count = gradeMap.getOrDefault(level, 0L);
            int percentage = (totalCount > 0)? (int) ((count * 100) / totalCount) : 0;

            serviceResponse.add(GradeDistributionResponse.builder()
                    .grade(level)
                    .count((int) count)
                    .percentage(percentage)
                    .build());
        }

        return serviceResponse;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardRecentActivities(Integer limit) {
        Map<String, Object> finalApiResponse = new HashMap<>();
        try {
            logger.debug("Start - getDashboardRecentActivities Service :: {}", limit);

            if (limit == null || limit <= 0) {
                limit = 10; // default limit
            }

            List<ActivityLogDomain> activityLogDomainList = activityLogService.getRecentActivitiesWithLimit(limit);

            finalApiResponse = ResponseUtils.formatAPIResponse("200", "Success", activityLogDomainList);
            logger.debug("Final API Response :: {}", finalApiResponse);
            return finalApiResponse;
        }catch (Exception e){
            logger.error("Exception - getDashboardRecentActivities :: {}", e.getMessage());
            finalApiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalApiResponse;
        }

    }

    public  Map<String, Object> getUserProfile(String userId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        UserProfileResponse userProfileResponse = new UserProfileResponse();

        try{
            logger.debug("Start - getUserProfile Service :: {}", userId);

            Optional<UserInfoDomain> userInfoDomain = userRepository.findUserInfoDomainByUserId(userId);
            if(userInfoDomain.isEmpty()){
                logger.error("User ID :: {} not found", userId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("404", "User Not Found", "");
                return finalServiceResponse;
            }

            UserInfoDomain userInfo = userInfoDomain.get();

            userProfileResponse.setId(userInfo.getUserId());
            userProfileResponse.setName(userInfo.getName());
            userProfileResponse.setEmail(userInfo.getEmail());
            userProfileResponse.setPhoneNumber(userInfo.getPhoneNumber());
            userProfileResponse.setAddress(userInfo.getAddress());
            userProfileResponse.setDateOfBirth(userInfo.getDateOfBirth());
            userProfileResponse.setGender(userInfo.getGender());
            userProfileResponse.setRole(userInfo.getRole().getRoleName());
            userProfileResponse.setProfileImageUrl(userInfo.getProfileImageUrl());

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "User Profile Retrieved Successfully", userProfileResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception - getUserProfile :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public  Map<String, Object> getStudentEnrolledSubject(String studentId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();

        List<StudentEnrolledSubjectResponse> studentEnrolledLists = new ArrayList<>();
        try{
            List<ClassroomDomain> classEnrollLists = classroomRepo.findClassroomDomainsByStudentIdAndStatus(studentId, VALUE_APPROVED);

            for(ClassroomDomain classroomDomain : classEnrollLists){
                logger.debug("Trying to get enrolled subject by student id :: {}", studentId);
                StudentEnrolledSubjectResponse studentEnrolledSubjectResponse = new StudentEnrolledSubjectResponse();

                Optional<ClassDomain> classDomainOptional = classRepo.findById(classroomDomain.getClassId());

                if(classDomainOptional.isEmpty()){
                    logger.error("Class ID :: {} not found", classroomDomain.getClassId());
                    continue;
                }
                ClassDomain classDomain = classDomainOptional.get();

                long subjectId = classDomain.getSubjectId();

                Optional<SubjectDomain> subjectDomainOptional = subjectRepository.findById(subjectId);

                if(subjectDomainOptional.isPresent()){
                    studentEnrolledSubjectResponse.setSubjectName(subjectDomainOptional.get().getSubjectName());
                    studentEnrolledSubjectResponse.setSubjectId(subjectDomainOptional.get().getSubjectCode());
                }

                Optional<UserInfoDomain> userInfoDomain = userRepository.findUserInfoDomainByUserId(classDomain.getTeacherId());

                String teacherName = userInfoDomain.isEmpty() ? "" : userInfoDomain.get().getName();
                studentEnrolledSubjectResponse.setTeacherName(teacherName);

                Optional<ExamDomain> nextExamOptional = examRepository.findFirstByClassIdAndExamDateAfterOrderByExamDateAsc(classDomain.getClassId(), LocalDateTime.now());

                if(nextExamOptional.isPresent()){
                    studentEnrolledSubjectResponse.setNextExamDate(String.valueOf(nextExamOptional.get().getExamDate()));
                }else{
                    studentEnrolledSubjectResponse.setNextExamDate(null);
                }

                studentEnrolledLists.add(studentEnrolledSubjectResponse);
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", studentEnrolledLists);
            logger.debug("Final Service Response :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception - getStudentEnrolledSubject :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getStudentRankingByExamId(long examId, String userId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Start - getStudentRankingByExamId Service :: {} :: {}", examId, userId);

            Optional<ExamDomain> examDomainOptional = examRepository.findById(examId);
            if(examDomainOptional.isEmpty()){
                logger.error("Exam ID :: {} not found", examId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("404", "Exam Not Found", "");
                return finalServiceResponse;
            }

            ExamDomain examDomain = examDomainOptional.get();

            List<ExamResultDomain> examResults = examResultRepository.findByExamIdOrderByScoreDesc(examId);

            int rank = 1;
            int studentRank = -1; // -1 indicates not found
            for (ExamResultDomain result : examResults) {
                if (result.getStudentId().equals(userId)) {
                    studentRank = rank;
                    break;
                }
                rank++;
            }

            if(studentRank == -1){
                logger.error("User ID :: {} not found in exam results for Exam ID :: {}", userId, examId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("404", "Student Exam Result Not Found", "");
                return finalServiceResponse;
            }

            StudentExamRankingResponse rankingResponse = StudentExamRankingResponse.builder()
                    .examId(examId)
                    .examName(examDomain.getExamTitle())
                    .rank(studentRank)
                    .build();

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", rankingResponse);
            logger.debug("Final Service Response :: {}", finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception - getStudentRankingByExamId :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getStudentRankingBySubjectId(long subjectId, String studentId) {
        try {
            logger.info("Calculating Subject Rank :: Subject: {}, Student: {}", subjectId, studentId);

            // 1. Fetch the full ranked list for the subject from the DB
            List<SubjectRankingProjection> rankings = examResultRepository.findSubjectRankingAcrossClass(subjectId);

            // 2. Find the specific student's record in the list
            SubjectRankingProjection studentStanding = rankings.stream()
                    .filter(r -> r.getStudentId().equals(studentId))
                    .findFirst()
                    .orElse(null);

            if (studentStanding == null) {
                return ResponseUtils.formatAPIResponse("404", "No graded results found for this student in this subject", "");
            }

            // 3. Map to DTO for Frontend
            SubjectRankingResponse response = SubjectRankingResponse.builder()
                    .studentId(studentStanding.getStudentId())
                    .totalScore(studentStanding.getTotalScore())
                    .rank(studentStanding.getFinalRank())
                    .totalParticipants(rankings.size())
                    .build();

            return ResponseUtils.formatAPIResponse("200", "Success", response);

        } catch (Exception e) {
            logger.error("Ranking Error for subject {}: {}", subjectId, e.getMessage());
            return ResponseUtils.formatAPIResponse("500", "Internal Server Error", "");
        }
    }

    public Map<String, Object> getStudentRankingBySubjectInClass(long subjectId, long classId, String studentId) {
        try {
            logger.info("Calculating Subject Rank In a class {} :: Subject: {}, Student: {}", classId, subjectId, studentId);

            // 1. Fetch the full ranked list for the subject from the DB
            List<SubjectRankingProjection> rankings = examResultRepository.findSubjectRankingInClass(subjectId, classId);

            // 2. Find the specific student's record in the list
            SubjectRankingProjection studentStanding = rankings.stream()
                    .filter(r -> r.getStudentId().equals(studentId))
                    .findFirst()
                    .orElse(null);

            if (studentStanding == null) {
                return ResponseUtils.formatAPIResponse("404", "No graded results found for this student in this subject", "");
            }

            // 3. Map to DTO for Frontend
            SubjectRankingResponse response = SubjectRankingResponse.builder()
                    .studentId(studentStanding.getStudentId())
                    .totalScore(studentStanding.getTotalScore())
                    .rank(studentStanding.getFinalRank())
                    .totalParticipants(rankings.size())
                    .build();

            return ResponseUtils.formatAPIResponse("200", "Success", response);

        } catch (Exception e) {
            logger.error("Ranking Error for subject {}: {}", subjectId, e.getMessage());
            return ResponseUtils.formatAPIResponse("500", "Internal Server Error", "");
        }
    }

}
