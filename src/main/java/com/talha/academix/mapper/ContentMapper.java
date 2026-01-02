package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.model.Content;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    
    @Mapping(source = "course.courseId", target = "courseId")
    @Mapping(source = "imageFile.id", target = "imageFileId")
    @Mapping(target = "imageSignedUrl", ignore = true)
    ContentDTO toDto(Content content);

    @Mapping(source = "courseId", target = "course.courseId")
    @Mapping(source = "imageFileId", target = "imageFile.id")
    Content toEntity(ContentDTO dto);
}
