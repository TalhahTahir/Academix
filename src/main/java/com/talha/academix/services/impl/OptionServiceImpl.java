package com.talha.academix.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.OptionDTO;
import com.talha.academix.dto.StudentOptionResponse;
import com.talha.academix.exception.InvalidAttemptException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Question;
import com.talha.academix.model.QuestionOption;
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
    private final ModelMapper mapper;

    @Override
    public OptionDTO addOption(Long userid, Long questionId, OptionDTO dto) {

        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
    
        if(courseService.teacherOwnership(userid, question.getExam().getCourse().getCourseid())){

        if ((dto.isCorrect())) {
            long count = optionRepo.countByQuestionIdAndIsCorrectTrue(questionId);
            if (count >= 1) {
                throw new InvalidAttemptException("Only one option can be marked as correct per question.");
            }
        }
    
        QuestionOption questionOption = mapper.map(dto, QuestionOption.class);
        questionOption.setQuestion(question);
        questionOption.setCorrect((dto.isCorrect()));
        questionOption = optionRepo.save(questionOption);
    
        return mapper.map(questionOption, OptionDTO.class);
    }
    else throw new RoleMismatchException("Only teacher can add options");
    }
    

    @Override
    public List<OptionDTO> getOptionsByQuestion(Long questionId) {
        return optionRepo.findByQuestionId(questionId)
            .stream()
            .map(o -> mapper.map(o, OptionDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public OptionDTO updateOption(Long userid, Long optionId, OptionDTO dto) {
        QuestionOption questionOption = optionRepo.findById(optionId)
            .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));

        Long questionId = questionOption.getQuestion().getId();
        Question question = questionRepo.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        
        if(courseService.teacherOwnership(userid, questionOption.getQuestion().getExam().getCourse().getCourseid())){

            mapper.getConfiguration().setSkipNullEnabled(true);
            mapper.map(dto, questionOption);
            questionOption.setQuestion(question);
            questionOption = optionRepo.save(questionOption);
    
            return mapper.map(questionOption, OptionDTO.class);
        }
        else throw new RoleMismatchException("Only teacher can update options");
    }

    @Override
    public void deleteOption(Long userid, Long optionId) {

        QuestionOption questionOption = optionRepo.findById(optionId)
            .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));

        if(courseService.teacherOwnership(userid, questionOption.getQuestion().getExam().getCourse().getCourseid())){
        optionRepo.delete(questionOption);
    }
    else throw new RoleMismatchException("Only teacher can delete options");
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
