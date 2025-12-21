package com.talha.academix.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.OptionDTO;
import com.talha.academix.dto.StudentOptionResponse;
import com.talha.academix.services.OptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OptionController {

    private final OptionService optionService;

    // Teacher: add option to question (teacher step 3)
    @PostMapping("/questions/{questionId}/options/teachers/{teacherId}")
    public OptionDTO addOption(@PathVariable Long teacherId,
                               @PathVariable Long questionId,
                               @RequestBody OptionDTO dto) {
        dto.setQuestionId(questionId);
        return optionService.addOption(teacherId, questionId, dto);
    }

    // Teacher: list options (includes isCorrect)
    @GetMapping("/questions/{questionId}/options/teachers/{teacherId}")
    public List<OptionDTO> getOptionsByQuestionTeacher(@PathVariable Long teacherId,
                                                       @PathVariable Long questionId) {
        return optionService.getOptionsByQuestion(questionId);
    }

    // Student: list options (NO isCorrect)
    @GetMapping("/questions/{questionId}/options")
    public List<StudentOptionResponse> getOptionsByQuestionStudent(@PathVariable Long questionId) {
        return optionService.getOptionsByQuestionForStudent(questionId);
    }

    // Teacher: update option
    @PutMapping("/options/{optionId}/teachers/{teacherId}")
    public OptionDTO updateOption(@PathVariable Long teacherId,
                                  @PathVariable Long optionId,
                                  @RequestBody OptionDTO dto) {
        return optionService.updateOption(teacherId, optionId, dto);
    }

    // Teacher: delete option
    @DeleteMapping("/options/{optionId}/teachers/{teacherId}")
    public void deleteOption(@PathVariable Long teacherId,
                             @PathVariable Long optionId) {
        optionService.deleteOption(teacherId, optionId);
    }
}