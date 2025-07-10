package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.OptionDTO;

public interface OptionService {
    OptionDTO addOption(Long userid, Long questionId, OptionDTO dto);
    List<OptionDTO> getOptionsByQuestion(Long questionId);
    OptionDTO updateOption(Long userid, Long optionId, OptionDTO dto);
    void deleteOption(Long userid, Long optionId);
}