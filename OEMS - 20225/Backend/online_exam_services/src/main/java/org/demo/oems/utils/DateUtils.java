package org.demo.oems.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DateUtils {
    private static final Logger logger = LogManager.getLogger(DateUtils.class);
    public static LocalDateTime formatDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate localDate = LocalDate.parse(dateString, formatter);

            logger.debug("Local Date :: {}", localDate.atStartOfDay());
            return localDate.atStartOfDay(); // 00:00:00
        }catch (Exception e){
            logger.error("Exception while parsing date format :: {}", e.getMessage());
            return null;
        }
    }

    public static LocalDateTime formatTimestamp(String timestampString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            return LocalDateTime.parse(timestampString, formatter);
        }catch (Exception e){
            logger.error("Exception while parsing date format :: {}", e.getMessage());
            return null;
        }
    }

    public static String convertDateToString(LocalDateTime date) {
        try {
            if (date == null) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);
            logger.debug("Formatted Date String :: {}", formattedDate);
            return formattedDate;
        } catch (Exception e) {
            logger.error("Exception while converting date to string :: {}", e.getMessage());
            return null;
        }
    }

    public static String convertTimestampToString(LocalDateTime date) {
        try {
            if (date == null) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            String formattedDate = date.format(formatter);
            logger.debug("Formatted Date String :: {}", formattedDate);
            return formattedDate;
        } catch (Exception e) {
            logger.error("Exception while converting date to string :: {}", e.getMessage());
            return null;
        }
    }

    public static String getClassStatus(LocalDateTime classStart, LocalDateTime classEnd) {
        try {
            logger.debug("Trying to compare start date and end start utils {} : {}", classStart, classEnd);
            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(classStart)) {
                return "NOT_STARTED";
            }

            if (now.isAfter(classEnd) || now.isEqual(classEnd)) {
                return "ENDED";
            }

            return "ONGOING";
        }catch (Exception e){
            logger.error("Exception while get Class Status :: {}", e.getMessage());
            return "";
        }
    }

    public static String getExamStatus(LocalDateTime examStart, int duration) {
        try {
            LocalDateTime examFinish = LocalDateTime.now().plusMinutes(duration);

            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(examStart)) {
                return "UP_COMING";
            }

            if (now.isAfter(examFinish) || now.isEqual(examFinish)) {
                return "COMPLETED";
            }

            return "ONGOING";

        }catch (Exception e){
            logger.error("Exception while get exam Status :: {}", e.getMessage());
            return "";
        }
    }

    public static int compareDateWithCurrent(LocalDateTime setDate){
        try {
            LocalDateTime currentDateTime = LocalDateTime.now();

            if (currentDateTime.isAfter(setDate)) {
                logger.debug("Current time is bigger the setting date");
                return -1;
            } else if (currentDateTime.isBefore(setDate)) {
                logger.debug("Current time is smaller the setting date");
                return 1;
            } else return 0;
        }catch (Exception e){
            logger.error("Exception while comparing current date with the setting date :: {}", e.getMessage());
            return -2;
        }
    }
}
