package com.talha.academix.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.OptionDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Option;
import com.talha.academix.model.Question;
import com.talha.academix.repository.OptionRepo;
import com.talha.academix.repository.QuestionRepo;
import com.talha.academix.services.OptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepo optionRepo;
    private final QuestionRepo questionRepo;
    private final ModelMapper modelMapper;

    @Override
    public OptionDTO addOption(Long questionId, OptionDTO dto) {
        Question question = questionRepo.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        Option option = modelMapper.map(dto, Option.class);
        option.setQuestion(question);
        option = optionRepo.save(option);

        return modelMapper.map(option, OptionDTO.class);
    }

    @Override
    public List<OptionDTO> getOptionsByQuestion(Long questionId) {
        return optionRepo.findByQuestionId(questionId)
            .stream()
            .map(o -> modelMapper.map(o, OptionDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public OptionDTO updateOption(Long optionId, OptionDTO dto) {
        Option option = optionRepo.findById(optionId)
            .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));
        
        option.setText(dto.getText());
        option.setCorrect(dto.isCorrect());
        option = optionRepo.save(option);

        return modelMapper.map(option, OptionDTO.class);
    }

    @Override
    public void deleteOption(Long optionId) {
        Option option = optionRepo.findById(optionId)
            .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));
        optionRepo.delete(option);
    }
}
