package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ActivityLogDomain;
import org.demo.oems.repository.ActivityLogRepo;
import org.demo.oems.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityLogService {

    private final ActivityLogRepo activityLogRepo;

    private static final Logger logger = LogManager.getLogger(ActivityLogService.class);

    @Autowired
    public ActivityLogService(ActivityLogRepo activityLogRepo) {
        this.activityLogRepo = activityLogRepo;
    }

    public void saveLoginActivity(String roleName, String userId, String userName, LocalDateTime localDateTime) {
        logger.debug("Start - saveLoginActivity :: roleName: {}, userId: {}, userName: {}, localDateTime: {}", roleName, userId, userName, localDateTime);
        ActivityLogDomain activityLogDomain = new ActivityLogDomain();
        activityLogDomain.setUserId(userId);
        activityLogDomain.setName(userName);

        String localDateTimeStr = DateUtils.convertTimestampToString(localDateTime);
        String action = roleName + " " + userName + " logged in at " + localDateTimeStr;

        logger.debug("action :: {}", action);

        activityLogDomain.setAction(action);
        activityLogDomain.setTimestamp(localDateTime);

        activityLogRepo.save(activityLogDomain);
        logger.info("Successfully saved login activity for userId: {}", userId);
    }

    public List<ActivityLogDomain> getAllActivityLogs() {
        return activityLogRepo.findAll();
    }

    public Optional<ActivityLogDomain> getActivityLogById(Long id) {
        return activityLogRepo.findById(id);
    }

    public void deleteActivityLogById(Long id) {
        activityLogRepo.deleteById(id);
    }

    public List<ActivityLogDomain> getRecentActivitiesWithLimit(Integer limit) {
     Pageable topTen = PageRequest.of(0, limit);
     return activityLogRepo.findRecentLogs(topTen);
    }
}
