package org.demo.oems.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.demo.oems.domain.*;
import org.demo.oems.payload.response.*;
import org.demo.oems.repository.*;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.demo.oems.utils.CommonConstantUtils.*;


@Service
@RequiredArgsConstructor
public class DashboardService {
    private final Logger logger = LogManager.getLogger(DashboardService.class);

    private final SubjectRepo subjectRepository;

    private final ExamRepo examRepository;

    private final UserInfoRepo userRepository;

    private final RoleRepo roleRepo;
    private final ClassroomRepo classroomRepo;
    private final ClassRepo classRepo;

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

    public List<GradeDistributionResponse> getOverallGradeDistribution(){
        List<GradeDistributionResponse> serviceResponse = new ArrayList<>();

        GradeDistributionResponse gradeDistributionResponse = new GradeDistributionResponse();

        String grade = "A";
        int count = 0;
        int percentage = 0;

        gradeDistributionResponse.setGrade(grade);
        gradeDistributionResponse.setCount(count);
        gradeDistributionResponse.setPercentage(percentage);

        serviceResponse.add(gradeDistributionResponse);

        return serviceResponse;
    }


    public List<RecentActivitiesResponse> getDashboardRecentActivities() {
        List<RecentActivitiesResponse> serviceResponse = new ArrayList<>();
        RecentActivitiesResponse recentActivitiesResponse = new RecentActivitiesResponse();

        recentActivitiesResponse.setAction("");
        recentActivitiesResponse.setId("");
        recentActivitiesResponse.setUser("");
        recentActivitiesResponse.setTimestamp("2026-01-17");
        recentActivitiesResponse.setSubject("");

        return serviceResponse;

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


//    public Map<String, Object> getStudentStats(String studentId) {
//        // 1. Enrolled subjects count (distinct subjects via classrooms)
//        long enrolledCount = classroomRepo.countDistinctSubjectByStudentIdAndStatus(studentId, "APPROVED");
//
//        // 2. Upcoming exams count (exams after now, for classes the student is in)
//        LocalDateTime now = LocalDateTime.now();
//        long upcomingCount = examRepository.countUpcomingExamsForStudent(studentId, now);
//
//        // 3. Average score (from past exams/grades)
//        Double avgScoreRaw = gradeRepo.findAverageScoreByStudentId(studentId);
//        int averageScore = avgScoreRaw != null ? avgScoreRaw.intValue() : 0;
//
//        // 4. Class rank (assuming you have a rank table or view)
//        int classRank = classRankRepo.findRankByStudentId(studentId)
//                .orElse(0);  // 0 if not ranked yet
//
//        // 5. Build summary
//        StudentDashboardSummary summary = StudentDashboardSummary.builder()
//                .enrolledSubjects((int) enrolledCount)
//                .upcomingExams((int) upcomingCount)
//                .averageScore(averageScore)
//                .classRank(classRank)
//                .build();
//
//        // 6. Final response map (exactly your format)
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "0");
//        response.put("data", summary);
//
//        logger.debug("Student dashboard summary for {}: {}", studentId, summary);
//        return response;
//    }
}
