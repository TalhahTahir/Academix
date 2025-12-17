package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureDTO {
    private Long lectureId;
    private Long contentId;
    private String title;
    private String duration;
    private Long storedFileId;
    private String videoSignedUrl;
}
