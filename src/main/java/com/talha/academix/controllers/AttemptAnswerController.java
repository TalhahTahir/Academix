package com.talha.academix.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.AttemptAnswerDTO;
import com.talha.academix.services.AttemptAnswerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttemptAnswerController {

    private final AttemptAnswerService attemptAnswerService;

    // Upsert answer for a question in an attempt
    @PutMapping("/attempts/{attemptId}/answers/{questionId}")
    public AttemptAnswerDTO submitAnswer(@PathVariable Long attemptId,
                                         @PathVariable Long questionId,
                                         @PathVariable Long selectedOptionId) {
        return attemptAnswerService.submitAnswer(attemptId, questionId, selectedOptionId);
    }

    // List answers for an attempt
    @GetMapping("/attempts/{attemptId}/answers")
    public List<AttemptAnswerDTO> getAnswersByAttempt(@PathVariable Long attemptId) {
        return attemptAnswerService.getAnswersByAttempt(attemptId);
    }
}