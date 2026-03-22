package org.demo.oems.service;

import org.demo.oems.payload.request.EssayGradingRequest;
import org.demo.oems.payload.response.GradingResult;
import org.demo.oems.payload.response.QuestionGradeDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;


@Service
public class AIGradingService {

    private static final Logger logger = LoggerFactory.getLogger(AIGradingService.class);
    private final ChatClient chatClient;

    public AIGradingService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * Function 1: Essay Assistant (Using deepseek-chat)
     */
    public GradingResult suggestEssayGrade(EssayGradingRequest request) {
        String promptText = """
                You are an expert English Writing Examiner for a Cambodian-Chinese school.
                Task: Evaluate the essay based on the topic and rubric provided.
                
                Scoring Guidelines:
                - If the content is irrelevant to the topic, deduct marks heavily.
                - Provide specific feedback in English, Khmer, and Chinese.
                
                RUBRIC: %s
                TOPIC: %s
                STUDENT ESSAY: %s
                """.formatted(request.getRubric(), request.getEssayTopic(), request.getEssay());

        return callDeepSeek("deepseek-chat", promptText);
    }

    public GradingResult suggestCodeGrade(String problemDesc, String studentCode) {
        logger.info("Starting AI Code Evaluation. Problem length: {} chars, Code length: {} chars",
                problemDesc.length(), studentCode.length());

        // 1. Manually build the prompt text to avoid the ST4 template engine bug
        String instructionText = """
                You are a strict Computer Science examiner.
                
                Task: Grade the student's code based on the problem description.
                Instructions for your JSON response:
                - obtainedScore: suggested mark (0-100)
                - totalPossibleScore: 100
                - summaryMessage: Brief feedback in English, Khmer, and Chinese.
                - details: A list with 1 item containing specific logic feedback.
                
                PROBLEM:
                %s
                
                STUDENT CODE:
                %s
                """.formatted(problemDesc, studentCode);

        logger.debug("instructionText :: {}", instructionText);
        // 2. Fix: Wrap in UserMessage to bypass the buggy ST4 parser in M6
        UserMessage message = new UserMessage(instructionText);

        try {
            return chatClient.prompt()
                    .system("You are a Computer Science TA.")
                    .messages(message) // Use.messages() instead of.user()
                    // 3. LOGGING: This prints the full JSON payload to your console
                    .advisors(new SimpleLoggerAdvisor())
                    .options(ChatOptions.builder()
                            .model("deepseek-reasoner") // DeepSeek-R1 for code logic
                            .temperature(0.1)
                            .build())
                    .call()
                    .entity(GradingResult.class);

        } catch (Exception e) {
            logger.error("AI GRADING CRITICAL FAILURE: {}", e.getMessage());
            return GradingResult.builder()
                    .summaryMessage("Evaluation failed: " + e.getMessage())
                    .obtainedScore(0)
                    .build();
        }
    }

    private GradingResult callDeepSeek(String model, String promptText) {
        // BYPASS: wrapping in UserMessage stops the "template string is not valid" error
        UserMessage userMessage = new UserMessage(promptText);

        try {
            return chatClient.prompt()
                    .messages(userMessage)
                    .options(ChatOptions.builder()
                            .model(model)
                            .temperature(0.1) // Low temperature for consistent grading
                            .build())
                    .call()
                    .entity(GradingResult.class); // Spring AI generates the JSON instructions automatically
        } catch (Exception e) {
            logger.error("DeepSeek API Error: {}", e.getMessage());
            return GradingResult.builder()
                    .summaryMessage("AI Evaluation failed: " + e.getMessage())
                    .obtainedScore(0)
                    .build();
        }
    }
}