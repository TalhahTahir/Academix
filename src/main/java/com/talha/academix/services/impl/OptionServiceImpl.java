package com.talha.academix.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.OptionDTO;
import com.talha.academix.dto.StudentOptionResponse;
import com.talha.academix.exception.InvalidAttemptException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.mapper.OptionMapper;
import com.talha.academix.model.Question;
import com.talha.academix.model.QuestionOption;
import com.talha.academix.repository.OptionRepo;
import com.talha.academix.repository.QuestionRepo;
import com.talha.academix.services.OptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepo optionRepo;
    private final QuestionRepo questionRepo;
    private final OptionMapper optionMapper;

    @Override
    public OptionDTO addOption(Long questionId, OptionDTO dto) {

        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        if ((dto.isCorrect())) {
            long count = optionRepo.countByQuestionIdAndIsCorrectTrue(questionId);
            if (count >= 1) {
                throw new InvalidAttemptException("Only one option can be marked as correct per question.");
            }
        }

        QuestionOption questionOption = optionMapper.toEntity(dto);
        questionOption.setQuestion(question);
        questionOption.setCorrect((dto.isCorrect()));
        questionOption = optionRepo.save(questionOption);

        return optionMapper.toDto(questionOption);
    }

    @Override
    public List<OptionDTO> getOptionsByQuestion(Long questionId) {
        return optionRepo.findByQuestionId(questionId)
                .stream()
                .map(o -> optionMapper.toDto(o))
                .collect(Collectors.toList());
    }

    @Override
    public OptionDTO updateOption(Long optionId, OptionDTO dto) {
        QuestionOption questionOption = optionRepo.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));

        Long questionId = questionOption.getQuestion().getId();
        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        optionMapper.updateOptionFromDto(dto, questionOption);
        questionOption.setQuestion(question);
        questionOption = optionRepo.save(questionOption);

        return optionMapper.toDto(questionOption);
    }

    @Override
    public void deleteOption(Long optionId) {

        QuestionOption questionOption = optionRepo.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));

        optionRepo.delete(questionOption);
    }

    @Override
    public List<StudentOptionResponse> getOptionsByQuestionForStudent(Long questionId) {
        List<QuestionOption> options = optionRepo.findByQuestionId(questionId);
        return options.stream()
                .map(o -> {
                    StudentOptionResponse response = new StudentOptionResponse();
                    response.setId(o.getId());
                    response.setText(o.getText());
                    response.setQuestionId(o.getQuestion().getId());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
