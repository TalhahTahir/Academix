package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CreateUserDTO;
import com.talha.academix.dto.UserDTO;

public interface UserService {
    UserDTO createUser(CreateUserDTO dto);
    UserDTO getUserById(Long userId);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long userId, CreateUserDTO dto);
    void deleteUser(Long userId);

    boolean adminValidation(Long userid);
}