package com.talha.academix.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDTO {
    private Long id;
    private Long examId;
    private Long studentId;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<Long> answerIds; // Representing answers by their IDs
}
