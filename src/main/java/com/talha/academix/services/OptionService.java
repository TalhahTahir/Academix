package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.OptionDTO;

public interface OptionService {
    OptionDTO addOption(Long questionId, OptionDTO dto);
    List<OptionDTO> getOptionsByQuestion(Long questionId);
    OptionDTO updateOption(Long optionId, OptionDTO dto);
    void deleteOption(Long optionId);
}