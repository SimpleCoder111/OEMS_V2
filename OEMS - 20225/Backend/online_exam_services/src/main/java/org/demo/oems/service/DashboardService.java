package org.demo.oems.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.demo.oems.domain.RoleDomain;
import org.demo.oems.payload.response.DashboardStatisticResponse;
import org.demo.oems.repository.ExamRepo;
import org.demo.oems.repository.RoleRepo;
import org.demo.oems.repository.SubjectRepo;
import org.demo.oems.repository.UserInfoRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.demo.oems.utils.CommonConstantUtils.*;


@Service
@RequiredArgsConstructor
public class DashboardService {
    private final Logger logger = LogManager.getLogger(DashboardService.class);

    private final SubjectRepo subjectRepository;

    private final ExamRepo examRepository;

    private final UserInfoRepo userRepository;

    private final RoleRepo roleRepo;

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



}
