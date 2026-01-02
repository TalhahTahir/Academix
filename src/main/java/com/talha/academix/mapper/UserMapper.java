package com.talha.academix.mapper;

import org.mapstruct.Mapper;

import com.talha.academix.dto.UserDTO;
import com.talha.academix.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDTO toDto(User user);

    User toEntity(UserDTO dto);
}
