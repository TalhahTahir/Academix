package com.talha.academix.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.OptionDTO;
import com.talha.academix.dto.StudentOptionResponse;
import com.talha.academix.services.OptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OptionController {

    private final OptionService optionService;

    // Teacher: add option to question (teacher step 3)
    @PreAuthorize("@questionSecurity.isQuestionOwner(principal, #questionId)")
    @PostMapping("/questions/{questionId}/options")
    public OptionDTO addOption(@PathVariable Long questionId,
                               @Valid @RequestBody OptionDTO dto) {
        dto.setQuestionId(questionId);
        return optionService.addOption(questionId, dto);
    }

    // Teacher: list options (includes isCorrect)
    @GetMapping("/questions/{questionId}/options")
    public List<OptionDTO> getOptionsByQuestionTeacher(@PathVariable Long questionId) {
        return optionService.getOptionsByQuestion(questionId);
    }

    // Student: list options (NO isCorrect)
    @GetMapping("/questions/{questionId}/options/student")
    public List<StudentOptionResponse> getOptionsByQuestionStudent(@PathVariable Long questionId) {
        return optionService.getOptionsByQuestionForStudent(questionId);
    }

    // Teacher: update option
    @PreAuthorize("@optionSecurity.isOptionOwner(principal, #optionId)")
    @PutMapping("/options/{optionId}")
    public OptionDTO updateOption(@PathVariable Long optionId,
                                  @Valid @RequestBody OptionDTO dto) {
        return optionService.updateOption(optionId, dto);
    }

    // Teacher: delete option
    @PreAuthorize("@optionSecurity.isOptionOwner(principal, #optionId)")
    @DeleteMapping("/options/{optionId}")
    public void deleteOption(@PathVariable Long optionId) {
        optionService.deleteOption(optionId);
    }
}