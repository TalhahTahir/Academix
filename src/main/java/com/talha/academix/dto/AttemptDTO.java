package com.talha.academix.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDTO {

    private Long id;
    @NotNull
    private Long examId;
    @NotNull
    private Long studentId;
    @NotNull
    private Instant startedAt;
    @NotNull
    private Instant completedAt;
    @NotEmpty
    private List<Long> answerIds;
}
