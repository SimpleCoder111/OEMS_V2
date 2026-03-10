package org.demo.oems.service;

import org.demo.oems.payload.response.GradingResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class AIGradingService {

    private final ChatClient chatClient;

    public AIGradingService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * Function 1: Essay Assistant (Using deepseek-chat for language fluency)
     */
    public GradingResult suggestEssayGrade(String rubric, String studentEssay) {
        return chatClient.prompt()
                .system("You are an expert HSK language evaluator. Suggest a mark (1-100) based on the rubric. Provide rationales in Khmer and Chinese.")
                .user(u -> u.text("RUBRIC: {rubric}\nESSAY: {essay}")
                        .param("rubric", rubric)
                        .param("essay", studentEssay))
                .options(OpenAiChatOptions.builder().model("deepseek-chat").build())
                .call()
                .entity(GradingResult.class); // Auto-maps JSON to the Java Record
    }

    /**
     * Function 2: Coding Assistant (Using deepseek-reasoner for logical debugging)
     */
    public GradingResult suggestCodeGrade(String problemDesc, String studentCode) {
        return chatClient.prompt()
                .system("You are a Computer Science TA. Analyze code logic, syntax, and efficiency. Suggest a mark (1-100). Provide rationales in Khmer and Chinese.")
                .user(u -> u.text("PROBLEM: {desc}\nCODE: {code}")
                        .param("desc", problemDesc)
                        .param("code", studentCode))
                .options(OpenAiChatOptions.builder().model("deepseek-reasoner").build())
                .call()
                .entity(GradingResult.class);
    }

}
