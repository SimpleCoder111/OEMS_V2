package org.demo.oems.rest;

import org.demo.oems.payload.request.CodeGradingRequest;
import org.demo.oems.payload.request.EssayGradingRequest;
import org.demo.oems.payload.response.GradingResult;
import org.demo.oems.service.AIGradingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIGradingRest {

    private final AIGradingService aiGradingService;

    public AIGradingRest(AIGradingService aiGradingService) {
        this.aiGradingService = aiGradingService;
    }

    @PostMapping("/grade-essay")
    public GradingResult gradeEssay(@RequestBody EssayGradingRequest request) {
        return aiGradingService.suggestEssayGrade(
                request.rubric(),
                request.essay()
        );
    }

    @PostMapping("/grade-code")
    public GradingResult gradeCode(@RequestBody CodeGradingRequest request) {
        return aiGradingService.suggestCodeGrade(
                request.problemDesc(),
                request.code()
        );
    }

}
