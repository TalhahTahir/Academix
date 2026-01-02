package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.DocumentDTO;
import com.talha.academix.model.Document;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    
    @Mapping(source = "content.contentId", target = "contentId")
    @Mapping(source = "storedFile.id", target = "storedFileId")
    @Mapping(target = "fileSignedUrl", ignore = true)
    DocumentDTO toDto(Document doc);

    @Mapping(source = "contentId", target = "content.contentId")
    @Mapping(source = "storedFileId", target = "storedFile.id")
    Document toEntity(DocumentDTO dto);
}
