package org.demo.oems.service;

import lombok.RequiredArgsConstructor;
import org.demo.oems.payload.response.ExamMonitorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(ExamMonitoringService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public void pushUpdate(ExamMonitorMessage message) {
        logger.info("Pushing update for examId: {}, studentId: {}, eventType: {}",
                message.getExamId(), message.getStudentId(), message.getEventType());

        String destination = "/topic/exam/"
                + message.getExamId();

        logger.info("destination: {}", destination);

        messagingTemplate.convertAndSend(destination, message);
    }
}

