package com.talha.academix.dto;

import java.time.Instant;
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
    private Instant startedAt;
    private Instant completedAt;
    private List<Long> answerIds;
}
