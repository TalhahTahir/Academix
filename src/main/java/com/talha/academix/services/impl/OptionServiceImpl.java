package com.talha.academix.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.OptionDTO;
import com.talha.academix.exception.InvalidAttemptException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Option;
import com.talha.academix.model.Question;
import com.talha.academix.repository.OptionRepo;
import com.talha.academix.repository.QuestionRepo;
import com.talha.academix.services.CourseService;
import com.talha.academix.services.OptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepo optionRepo;
    private final QuestionRepo questionRepo;
    private final CourseService courseService;
    private final ModelMapper modelMapper;

    @Override
    public OptionDTO addOption(Long userid, Long questionId, OptionDTO dto) {

        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
    
        if(courseService.teacherOwnership(userid, question.getExam().getCourse().getCourseid())){

        if (dto.isCorrect()) {
            long count = optionRepo.countByQuestionIdAndIsCorrectTrue(questionId);
            if (count > 1) {
                throw new InvalidAttemptException("Only one option can be marked as correct per question.");
            }
        }
    
        Option option = modelMapper.map(dto, Option.class);
        option.setQuestion(question);
        option = optionRepo.save(option);
    
        return modelMapper.map(option, OptionDTO.class);
    }
    else throw new RoleMismatchException("Only teacher can add options");
    }
    

    @Override
    public List<OptionDTO> getOptionsByQuestion(Long questionId) {
        return optionRepo.findByQuestionId(questionId)
            .stream()
            .map(o -> modelMapper.map(o, OptionDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public OptionDTO updateOption(Long userid, Long optionId, OptionDTO dto) {
        Option option = optionRepo.findById(optionId)
            .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));
        
        if(courseService.teacherOwnership(userid, option.getQuestion().getExam().getCourse().getCourseid())){
            option.setText(dto.getText());
            option.setCorrect(dto.isCorrect());
            option = optionRepo.save(option);
    
            return modelMapper.map(option, OptionDTO.class);
        }
        else throw new RoleMismatchException("Only teacher can update options");
    }

    @Override
    public void deleteOption(Long userid, Long optionId) {

        Option option = optionRepo.findById(optionId)
            .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));

        if(courseService.teacherOwnership(userid, option.getQuestion().getExam().getCourse().getCourseid())){
        optionRepo.delete(option);
    }
    else throw new RoleMismatchException("Only teacher can delete options");
}
}
