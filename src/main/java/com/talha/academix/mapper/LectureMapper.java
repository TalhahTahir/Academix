package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.LectureDTO;
import com.talha.academix.model.Lecture;

@Mapper(componentModel = "spring")
public interface LectureMapper {
    
    @Mapping(source = "content.contentId", target = "contentId")
    @Mapping(source = "videoUrl.id", target = "storedFileId")
    @Mapping(target = "videoSignedUrl", ignore = true)
    LectureDTO toDto(Lecture lecture);

    @Mapping(source = "contentId", target = "content.contentId")
    @Mapping(source = "storedFileId", target = "videoUrl.id")
    Lecture toEntity(LectureDTO dto);
}
