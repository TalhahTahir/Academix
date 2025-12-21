package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.OptionDTO;
import com.talha.academix.dto.StudentOptionResponse;

public interface OptionService {
    OptionDTO addOption(Long userid, Long questionId, OptionDTO dto);
    List<OptionDTO> getOptionsByQuestion(Long questionId);
    OptionDTO updateOption(Long userid, Long optionId, OptionDTO dto);
    List<StudentOptionResponse> getOptionsByQuestionForStudent(Long questionId);
    void deleteOption(Long userid, Long optionId);
}