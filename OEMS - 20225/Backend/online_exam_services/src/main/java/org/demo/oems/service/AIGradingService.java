package org.demo.oems.service;

import org.demo.oems.payload.request.EssayGradingRequest;
import org.demo.oems.payload.response.GradingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;


@Service
public class AIGradingService {

    private static final Logger logger = LoggerFactory.getLogger(AIGradingService.class);
    private final ChatClient chatClient;

    // 1. Initialize the converter for your specific class
    private final BeanOutputConverter<GradingResult> gradingConverter =
            new BeanOutputConverter<>(GradingResult.class);

    public AIGradingService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * Function 1: Essay Assistant (Using deepseek-chat)
     */
    public GradingResult suggestEssayGrade(EssayGradingRequest request) {
        logger.info("Start - suggestEssayGrade service with request :: {}", request.getEssayTitle());
        String promptText = """
                You are an expert English Writing Examiner for a Cambodian-Chinese school.
                Task: Evaluate the essay based on the topic and rubric provided.
                
                Scoring Guidelines:
                - If the content is irrelevant to the topic, deduct marks heavily.
                - Provide specific feedback in English.
                
                RUBRIC: %s
                TOPIC: %s
                STUDENT ESSAY: %s
                """.formatted(request.getRubric(), request.getEssayTitle(), request.getEssay());

        return callDeepSeek("deepseek-chat", promptText);
    }

    public GradingResult suggestCodeGrade(String problemDesc, String studentCode) {
        logger.info("Starting AI Code Evaluation. Problem length: {} chars", problemDesc.length());

        // 2. Combine your text with the AI's JSON format instructions manually
        String promptText = """
                You are a strict Computer Science examiner. If the code is function correctly give full marks.
                Task: Grade the student's code based on the problem description.
                
                PROBLEM: %s
                STUDENT CODE: %s
                
                %s
                """.formatted(problemDesc, studentCode, gradingConverter.getFormat());

        try {
            // 3. Use.call().content() to get raw text instead of.entity()
            String rawJson = chatClient.prompt(promptText)
                    .advisors(new SimpleLoggerAdvisor())
                    .options(ChatOptions.builder()
                            .model("deepseek-reasoner") // DeepSeek-R1
                            .temperature(0.1)
                            .build())
                    .call()
                    .content();

            // 4. Clean the response (DeepSeek-R1 sometimes includes <think> tags or markdown)
            String cleanedJson = rawJson.replaceAll("```json|```", "").trim();
            if (cleanedJson.contains("</think>")) {
                cleanedJson = cleanedJson.substring(cleanedJson.lastIndexOf("</think>") + 8).trim();
            }

            // 5. Convert to Java Object manually
            return gradingConverter.convert(cleanedJson);

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